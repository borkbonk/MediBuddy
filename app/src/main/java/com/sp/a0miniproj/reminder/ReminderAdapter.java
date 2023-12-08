package com.sp.a0miniproj.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.a0miniproj.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminderid> reminderList;
    private Context context;
    private OnReminderClickListener onReminderClickListener;

    public ReminderAdapter(List<Reminderid> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    public void setOnReminderClickListener(OnReminderClickListener listener) {
        this.onReminderClickListener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminderid reminder = reminderList.get(position);
        holder.titleTextView.setText(reminder.getTitle());
        holder.descriptionTextView.setText(reminder.getDescription());

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = sdfDate.format(new Date(reminder.getTimestamp()));
        holder.dateTextView.setText(formattedDate);

        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdfTime.format(new Date(reminder.getTimestamp()));
        holder.timeTextView.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView, descriptionTextView, dateTextView, timeTextView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            timeTextView = itemView.findViewById(R.id.textViewTime);

            // Set a click listener on the item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the clicked reminder
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onReminderClickListener != null) {
                // Trigger the onReminderClick event and pass the reminder ID
                long reminderId = reminderList.get(position).getId();
                onReminderClickListener.onReminderClick(reminderId);
            }
        }
    }

    // Interface to handle click events on the reminder items
    public interface OnReminderClickListener {
        void onReminderClick(long reminderId);
    }
}
