package com.cherrydev.airsend.app.messages;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.AppViewModel;
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.database.models.SentMessage;
import com.cherrydev.airsend.app.utils.DialogRecyclerViewAction;
import com.cherrydev.airsend.databinding.DialogSentMessagesBinding;
import io.github.romansj.core.MessageType;
import io.github.romansj.core.client.ClientManager;
import com.cherrydev.common.ClipboardUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;


public class FragmentSentMessages extends Fragment {


    private DialogSentMessagesBinding binding;
    private AppViewModel viewModel;

    public static FragmentSentMessages newInstance() {
        return new FragmentSentMessages();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSentMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        initMessageList();
    }


    private void initMessageList() {
        OnClickListener<SentMessage> clickListener = new OnClickListener<>() {
            @Override
            public void onClick(SentMessage message) {
                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), message.getText());
                Toast.makeText(requireContext(), getString(R.string.copied_message_text), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(SentMessage message) {
                DialogRecyclerViewAction.DialogButtonListener<MessageActionWrapper> listener = item -> handleMessageActionChoice(message, item.getMessageAction());

                List<MessageActionWrapper> list = List.of(
                        new MessageActionWrapper(MessageAction.RESEND, getString(R.string.resend)),
                        new MessageActionWrapper(MessageAction.COPY_IP, getString(R.string.copy_IP)),
                        new MessageActionWrapper(MessageAction.COPY_MESSAGE, getString(R.string.copy_message)),
                        new MessageActionWrapper(MessageAction.DELETE, getString(R.string.delete)));

                DialogRecyclerViewAction<MessageActionWrapper> dialog = DialogRecyclerViewAction.newInstance(getString(R.string.message_actions), getString(R.string.what_would_you_like_to_do_with_the_message), list, "", getString(R.string.cancel), listener);
                dialog.show(getParentFragmentManager(), null);
            }
        };




        var messageTypes = List.of(MessageType.MESSAGE, MessageType.CONNECT);
        initFilter2(binding.dropdownMssgType.getEditText(), "Type",messageTypes.stream().map(Enum::name).collect(Collectors.toList()));
        var dateAdapter = initFilter2(binding.dropdownMssgDate.getEditText(), "Date");
        MyApplication.getDatabaseManager().getDb().getUniqueDates().observe(getViewLifecycleOwner(), dates -> {
            dateAdapter.clear();
            dateAdapter.addAll(dates);
        });
        setFilter(null, null, messageTypes);




        MessageAdapter<SentMessage> adapter = new MessageAdapter<>(clickListener);
        adapter.setHasStableIds(true);
        viewModel.getSentMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateData(messages);
            binding.include.tvEmptyRvMessages.setVisibility(messages.size() == 0 ? View.VISIBLE : View.GONE);
            binding.include.recyclerView.setVisibility(messages.size() != 0 ? View.VISIBLE : View.GONE);
        });


        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.include.recyclerView.setAdapter(adapter);
    }

    ArrayAdapter<String> initFilter2(EditText editText, String emptyText) {
       return initFilter2(editText, emptyText, new ArrayList<>());
    }

    ArrayAdapter<String> initFilter2(EditText editText, String emptyText, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, values);

        ((AutoCompleteTextView) editText).setAdapter(adapter);
        ((AutoCompleteTextView) editText).setText(emptyText, false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.d("onTextChanged " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return adapter;
    }



    private void setFilter(LocalDateTime dateFrom, LocalDateTime dateUntil, List<MessageType> types) {
        viewModel.setSentMessageFilter(dateFrom, dateUntil, types);
    }

    private void handleMessageActionChoice(SentMessage message, MessageAction messageAction) {
        switch (messageAction) {
            case RESEND:
                message.setRetryCount(0);
                ClientManager.getInstance().messageClient(message.getIP(), message.getPort(), message.getText());
                break;

            case COPY_IP:
                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), message.getIP());
                Toast.makeText(requireContext(), R.string.copied_IP, Toast.LENGTH_LONG).show();
                break;
            case COPY_MESSAGE:
                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), message.getText());
                Toast.makeText(requireContext(), requireContext().getString(R.string.copied_message_text), Toast.LENGTH_LONG).show();
                break;
            case DELETE:
                Toast.makeText(requireContext(), "Didn't delete", Toast.LENGTH_LONG).show();
                //databaseManager.deleteMessage(message.getId()).runInBackground().run();
                break;
        }
    }

}