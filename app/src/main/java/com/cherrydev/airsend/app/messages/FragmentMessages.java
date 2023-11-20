package com.cherrydev.airsend.app.messages;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.AppViewModel;
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.database.models.UserMessage;
import com.cherrydev.airsend.app.messages.recipient.DialogChooseRecipients;
import com.cherrydev.airsend.app.utils.DialogRecyclerViewAction;
import com.cherrydev.airsend.app.utils.NetworkUtils;
import com.cherrydev.airsend.databinding.FragmentMessagesBinding;
import com.cherrydev.airsendcore.core.client.ClientManager;
import com.cherrydev.common.ClipboardUtils;
import com.cherrydev.dialogs.confirm.DialogConfirm;
import com.cherrydev.dialogs.utils.WrapperDialogFragment;
import com.cherrydev.keyboard.KeyboardUtils;
import com.google.android.material.button.MaterialButton;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FragmentMessages extends Fragment {

    private static final String KEY_RECIPIENT_CHOICE = "KEY_RECIPIENT_CHOICE";
    public static final String KEY_SELECTED_DEVICES = "KEY_SELECTED_DEVICES";
    public static final String KEY_MESSAGE_INPUT_TEXT = "KEY_MESSAGE_INPUT_TEXT";


    private FragmentMessagesBinding binding;
    private AppViewModel viewModel;
    private MessageViewModel messageViewModel;

    public static FragmentMessages newInstance() {
        FragmentMessages fragment = new FragmentMessages();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        messageViewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);


        // recovery from process death
        restoreState(savedInstanceState);


        initMessageList();

        initDropDown();

        initMessageSending();


        MaterialButton btnPasteMessage = binding.btnPasteMsg;
        btnPasteMessage.setOnClickListener(v -> {
            String clipboardText = ClipboardUtils.getClipboardText(MyApplication.getInstance());
            if (!clipboardText.isEmpty()) binding.inputLayoutMessage.getEditText().setText(clipboardText);
        });


        binding.btnDeleteMessages.setOnClickListener(v -> {
            var confirmDialog = DialogConfirm.newInstance(getString(R.string.delete_messages_question), getString(R.string.this_action_cannot_be_undone),
                    getString(R.string.delete), getString(R.string.go_back), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);

            confirmDialog.setListener(() -> databaseManager.deleteAllMessages().runInBackground().run());

            confirmDialog.show(getParentFragmentManager(), null);
        });


        listenToTextSendEvents();


        binding.btnSentMessages.setOnClickListener(v -> {
            WrapperDialogFragment.newInstance(
                    FragmentSentMessages.newInstance()
            ).show(getParentFragmentManager(), null);
        });
    }


    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            var recipientChoice = savedInstanceState.getString(KEY_RECIPIENT_CHOICE);
            var selectedDeviceIPs = savedInstanceState.getStringArrayList(KEY_SELECTED_DEVICES);
            var messageInputText = savedInstanceState.getString(KEY_MESSAGE_INPUT_TEXT);

            messageViewModel.setRecipientChoice(recipientChoice);
            databaseManager.getDb().getAllDevices(selectedDeviceIPs).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(devices -> messageViewModel.setSelectedDevices(devices));
            binding.inputLayoutMessage.getEditText().setText(messageInputText);

        } else { // navigation replace
            binding.inputLayoutMessage.getEditText().setText(messageViewModel.getMessageInputText());
        }
    }

    Handler handler;

    Handler getHandler() {
        if (handler == null) handler = new Handler();
        return handler;
    }


    private void initDropDown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, messageViewModel.getRecipientChoices());
        var recipientEditText = binding.dropdownRecipient.getEditText();

        ((AutoCompleteTextView) recipientEditText).setAdapter(adapter);
        ((AutoCompleteTextView) recipientEditText).setText(messageViewModel.getRecipientChoice(), false);
//        binding.btnPickRecipient.setVisibility(messageViewModel.getRecipientChoice().equals(messageViewModel.getRecipientChoices().get(1)) ? View.VISIBLE : View.GONE);


        var recipientDialogListener = new DialogChooseRecipients.DialogListener() {
            @Override
            public void onDialogClosed(List<Device> selected) {
                messageViewModel.setSelectedDevices(selected);


                if (selected.isEmpty()) {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.please_select_devices), Toast.LENGTH_LONG).show();
                    ((AutoCompleteTextView) recipientEditText).setText(messageViewModel.getRecipientChoices().get(0), false);
                    return;
                }

                getHandler().postDelayed(() -> KeyboardUtils.showKeyboard(binding.inputLayoutMessage.getEditText(), requireContext()), 100);
            }

        };


        recipientEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageViewModel.setRecipientChoice(s.toString());
                binding.btnPickRecipient.setVisibility(messageViewModel.getRecipientChoice().equals(messageViewModel.getRecipientChoices().get(1)) ? View.VISIBLE : View.GONE);


                if (messageViewModel.getRecipientChoice().equals(messageViewModel.getRecipientChoices().get(1))) {
                    if (!messageViewModel.getSelectedDevices().isEmpty()) return; // dont ask to select when already selected devices before
                    showSelectRecipientDialog(recipientDialogListener);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.btnPickRecipient.setOnClickListener(v -> showSelectRecipientDialog(recipientDialogListener));
    }


    private void listenToTextSendEvents() {
        viewModel.getTextToSend().observe(getViewLifecycleOwner(), text -> {
            if (text == null) return;
            viewModel.setTextToSend(null); //to consume i.e. reset state, so upon navigating back this wont be a new event (it will get the current value of variable each time fragment is opened)

            sendMessage(text);
        });


        viewModel.getNeedToCopyClipboard().observe(getViewLifecycleOwner(), needToCopy -> {
            if (!needToCopy) return; // to ignore state clearing
            viewModel.setNeedToCopyClipboard(false); //clear state


            String clipboardText = ClipboardUtils.getClipboardText(requireContext());
            if (clipboardText.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_clipboard_can_write), Toast.LENGTH_LONG).show();
                return;
            }

            sendMessage(clipboardText);
        });
    }


    void sendMessage(String text) {
        ClientManager instance = ClientManager.getInstance();
        databaseManager.getDb().getAllDevices().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(params -> {
            var pairs = params.stream().map(p -> Pair.of(p.getIP(), p.getPort())).collect(Collectors.toList());
            instance.messageClients(pairs, text);
        });
    }


    private void initMessageSending() {
        MaterialButton btnSend = binding.send;

        btnSend.setOnClickListener(v -> {
            if (isNotConnected()) return;


            String text = binding.inputLayoutMessage.getEditText().getText().toString().trim();

            if (text.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_required_fields), Toast.LENGTH_LONG).show();
                return;
            }

            // your devices
            if (Objects.equals(messageViewModel.getRecipientChoice(), messageViewModel.getRecipientChoices().get(1))) {
                var pairs = messageViewModel.getSelectedDevices().stream().map(p -> Pair.of(p.getIP(), p.getPort())).collect(Collectors.toList());
                ClientManager.getInstance().messageClients(pairs, text);

            } else {
                databaseManager.getDb().getAllDevices().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(devices -> {
                    var pairs = devices.stream().map(p -> Pair.of(p.getIP(), p.getPort())).collect(Collectors.toList());
                    ClientManager.getInstance().messageClients(pairs, text);
                });
            }


            messageViewModel.setMessageInputText("");
            binding.inputLayoutMessage.getEditText().getText().clear();
            Toast.makeText(requireContext(), getString(R.string.message_sent), Toast.LENGTH_LONG).show();
        });
    }

    private boolean isNotConnected() {
        if (!NetworkUtils.isConnectedToNetwork(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.not_connected_to_network), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void initMessageList() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        OnClickListener<UserMessage> clickListener = new OnClickListener<>() {
            @Override
            public void onClick(UserMessage message) {
                var messageText = message.getText();
                var isLink = Patterns.WEB_URL.matcher(messageText).matches();

                var settingOpenLinks = sharedPreferences.getBoolean(getString(R.string.setting_open_links_on_click), true);
                if (isLink && settingOpenLinks) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(messageText)));
                    return;
                }

                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), messageText);
                Toast.makeText(requireContext(), getString(R.string.copied_message_text), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(UserMessage message) {
                DialogRecyclerViewAction.DialogButtonListener<MessageActionWrapper> listener = item -> handleMessageActionChoice(message, item.getMessageAction());

                List<MessageActionWrapper> list = List.of(
                        new MessageActionWrapper(MessageAction.COPY_IP, getString(R.string.copy_IP)),
                        new MessageActionWrapper(MessageAction.COPY_MESSAGE, getString(R.string.copy_message)),
                        new MessageActionWrapper(MessageAction.DELETE, getString(R.string.delete)));

                DialogRecyclerViewAction<MessageActionWrapper> dialog = DialogRecyclerViewAction.newInstance(getString(R.string.message_actions), getString(R.string.what_would_you_like_to_do_with_the_message), list, "", getString(R.string.cancel), listener);
                dialog.show(getParentFragmentManager(), null);
            }
        };

        MessageAdapter<UserMessage> adapter = new MessageAdapter<>(clickListener);
        adapter.setHasStableIds(true);
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateData(messages);
            binding.include.tvEmptyRvMessages.setVisibility(messages.size() == 0 ? View.VISIBLE : View.GONE);
            binding.include.recyclerView.setVisibility(messages.size() != 0 ? View.VISIBLE : View.GONE);
        });

        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.include.recyclerView.setAdapter(adapter);
    }

    private void handleMessageActionChoice(UserMessage message, MessageAction messageAction) {
        switch (messageAction) {
            case COPY_IP:
                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), message.getIP());
                Toast.makeText(requireContext(), R.string.copied_IP, Toast.LENGTH_LONG).show();
                break;
            case COPY_MESSAGE:
                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), message.getText());
                Toast.makeText(requireContext(), requireContext().getString(R.string.copied_message_text), Toast.LENGTH_LONG).show();
                break;
            case DELETE:
                databaseManager.deleteMessage(message.getId()).runInBackground().run();
                break;
        }
    }


    private void showSelectRecipientDialog(DialogChooseRecipients.DialogListener dialogListener) {
        databaseManager.getDb().getAllDevices().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(devices -> {
            var dialog = DialogChooseRecipients.newInstance(devices, messageViewModel.getSelectedDevices(), dialogListener);
            dialog.show(getParentFragmentManager(), null);
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        messageViewModel.setMessageInputText(binding.inputLayoutMessage.getEditText().getText().toString());
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_RECIPIENT_CHOICE, messageViewModel.getRecipientChoice());

        var selectedDeviceIPs = new ArrayList<>(messageViewModel.getSelectedDevices().stream().map(device -> device.getIP()).collect(Collectors.toList()));
        outState.putStringArrayList(KEY_SELECTED_DEVICES, selectedDeviceIPs);

        outState.putString(KEY_MESSAGE_INPUT_TEXT, binding.inputLayoutMessage.getEditText().getText().toString());

        super.onSaveInstanceState(outState);
    }
}