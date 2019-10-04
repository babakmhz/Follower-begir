package com.follow.irani.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.follow.irani.Adapters.TicketsAnswerAdapter;
import com.follow.irani.App;
import com.follow.irani.Manager.JsonManager;
import com.follow.irani.Models.Messages;
import com.follow.irani.R;
import com.follow.irani.databinding.DialogTicketAnswersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class TicketAnswerDialog extends DialogFragment {

    private final int ticketId;
    private DialogTicketAnswersBinding binding;

    public TicketAnswerDialog(int ticketId) {
        this.ticketId = ticketId;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_ticket_answers, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //endregion

        binding.imvSend.setOnClickListener(v -> send());


        try {
            getTickets();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    private void getTickets() {
        ArrayList<Messages> messages = new ArrayList<>();

        final String requestBody = JsonManager.specificTicket(ticketId);

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "message/ticket/get", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response1);
                    binding.ticketNumber.setText(jsonObject.getInt("id") + "");
                    binding.ticketDesc.setText(jsonObject.getString("description"));
                    binding.ticketTime.setText(jsonObject.getString("created_at"));
                    binding.ticketTitle.setText(jsonObject.getString("title"));
                    if (jsonObject.getInt("status") == 1)
                        binding.ticketStatus.setText("خوانده شده");
                    else
                        binding.ticketStatus.setText("خوانده نشده");
                    JSONArray answers = jsonObject.getJSONArray("answer");
                    if (answers == null || answers.length() == 0) {
                        binding.llNoMessage.setVisibility(View.VISIBLE);
                    } else {
                        binding.llNoMessage.setVisibility(View.GONE);
                        for (int i = 0; i < answers.length(); i++) {
                            JSONObject object = answers.getJSONObject(i);
                            Messages ticket = new Messages();
                            ticket.setId(object.getInt("id"));
                            ticket.setStatus(object.getInt("status"));
                            ticket.setTitle(object.getString("title"));
                            ticket.setCreated_at(object.getString("created_at"));
                            ticket.setUser_name(object.getInt("user_name"));
                            ticket.setDescription(object.getString("description"));
                            messages.add(ticket);
                        }
                        setView(messages);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        App.requestQueue.add(request);
    }

    private void setView(ArrayList<Messages> messageList) {
        DividerItemDecoration decoration = new DividerItemDecoration(App.currentActivity, DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(App.currentActivity, LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(App.currentActivity, R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(App.currentActivity, 3);
        TicketsAnswerAdapter adapter = new TicketsAnswerAdapter(App.currentActivity, messageList);
        binding.rcvMessages.setLayoutManager(mLayoutManager);
        binding.rcvMessages.setItemAnimator(new DefaultItemAnimator());
        binding.rcvMessages.setAdapter(adapter);
        binding.rcvMessages.addItemDecoration(decoration);
        binding.rcvMessages.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvMessages.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvMessages.getChildCount(); i++) {
                    View v = binding.rcvMessages.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });
    }


    private void send() {
        if (TextUtils.isEmpty(binding.edtNewMessage.getText().toString())) {
            Toast.makeText(App.currentActivity, "متن ر وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        final String requestBody = JsonManager.reply(binding.ticketTitle.getText().toString(), binding.edtNewMessage.getText().toString(), ticketId);

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "message/ticket/set", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optBoolean("status")) {
                        binding.edtNewMessage.setText("");
                        getTickets();


                    } else
                        Toast.makeText(App.currentActivity, "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    Toast.makeText(App.currentActivity, "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        App.requestQueue.add(request);
    }

}
