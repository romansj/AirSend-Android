package com.cherrydev.airsend.app.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.AppViewModel;
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.database.models.SentMessage;
import com.cherrydev.airsend.app.utils.DialogRecyclerViewAction;
import com.cherrydev.airsend.databinding.DialogSentMessagesBinding;
import com.cherrydev.clipboard.ClipboardUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import io.github.romansj.core.client.ClientManager;


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


    class ListFilter {
        LocalDate dateFrom;
        LocalDate dateTo;
        LocalTime timeFrom;
        LocalTime timeTo;

        public LocalDate getDateFrom() {
            return dateFrom;
        }

        public void setDateFrom(LocalDate dateFrom) {
            this.dateFrom = dateFrom;
        }

        public LocalDate getDateTo() {
            return dateTo;
        }

        public void setDateTo(LocalDate dateTo) {
            this.dateTo = dateTo;
        }

        public LocalTime getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(LocalTime timeFrom) {
            this.timeFrom = timeFrom;
        }

        public LocalTime getTimeTo() {
            return timeTo;
        }

        public void setTimeTo(LocalTime timeTo) {
            this.timeTo = timeTo;
        }
    }

    private void initMessageList() {
        var adapter = new MessageAdapter<SentMessage>();
        adapter.setOnClickListener(getOnClickListener());
        adapter.setHasStableIds(true);

        viewModel.getSentMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateData(messages);
            updateListVisibility(messages);
        });

        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.include.recyclerView.setAdapter(adapter);
    }

    private void updateListVisibility(List<SentMessage> messages) {
        var listEmpty = messages.isEmpty();
        binding.include.tvEmptyRvMessages.setVisibility(listEmpty ? View.VISIBLE : View.GONE);
        binding.include.recyclerView.setVisibility(listEmpty ? View.GONE : View.VISIBLE);
    }

    @NonNull
    private OnClickListener<SentMessage> getOnClickListener() {
        return new OnClickListener<>() {
            @Override
            public void onClick(SentMessage message) {
                ClipboardUtils.copyToClipboard(MyApplication.getInstance(), message.getText());
                Toast.makeText(requireContext(), getString(R.string.copied_message_text), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(SentMessage message) {
                showMessageActionDialog(message);
            }
        };
    }

    List<MessageActionWrapper> messageActionsList = List.of(
//            new MessageActionWrapper(MessageAction.RESEND, getString(R.string.resend)),
//            new MessageActionWrapper(MessageAction.COPY_IP, getString(R.string.copy_IP)),
//            new MessageActionWrapper(MessageAction.COPY_MESSAGE, getString(R.string.copy_message)),
//            new MessageActionWrapper(MessageAction.DELETE, getString(R.string.delete))
    );

    private void showMessageActionDialog(SentMessage message) {
        var dialog = DialogRecyclerViewAction.newInstance(
                getString(R.string.message_actions),
                getString(R.string.what_would_you_like_to_do_with_the_message),
                messageActionsList, "", getString(R.string.cancel),
                item -> handleMessageAction(message, item.getMessageAction()));

        showDialog(dialog);
    }

    private void showDialog(DialogFragment dialog) {
        dialog.show(getParentFragmentManager(), null);
    }


    private void handleMessageAction(SentMessage message, MessageAction messageAction) {
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