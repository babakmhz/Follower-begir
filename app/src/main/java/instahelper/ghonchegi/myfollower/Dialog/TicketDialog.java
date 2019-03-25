package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import instahelper.ghonchegi.myfollower.Adapters.TicketsAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Interface.NewMessageSubmittedInterface;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Models.Messages;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogSupportBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class TicketDialog extends DialogFragment implements NewMessageSubmittedInterface {

    NewMessageSubmittedInterface callback;
    private DialogSupportBinding binding;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_support, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        callback = this;
        //endregion

        binding.fabAdd.setOnClickListener(v -> {
            NewMessageDialog newMessageDialog = new NewMessageDialog(callback);
            newMessageDialog.show(getChildFragmentManager(), "");
        });
        binding.imvArrowLeft.setOnClickListener(v -> dialog.dismiss());

        getTickets();
        return dialog;
    }


    private void getTickets() {
        ArrayList<Messages> messages = new ArrayList<>();

        final String requestBody = JsonManager.simpleJson();

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "message/ticket/list", response1 -> {
            if (response1 != null) {
                try {
                    JSONArray jsonArray = new JSONArray(response1);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Messages ticket = new Messages();
                        ticket.setId(jsonObject.getInt("id"));
                        ticket.setStatus(jsonObject.getInt("status"));
                        ticket.setTitle(jsonObject.getString("title"));
                        ticket.setCreated_at(jsonObject.getString("created_at"));
                        messages.add(ticket);
                    }
                    setView(messages);

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
        requestQueue.add(request);
    }

    private void setView(ArrayList<Messages> messageList) {
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        TicketsAdapter adapter = new TicketsAdapter(getContext(), messageList);
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

    @Override
    public void sumbited(boolean state) {
        if (state) getTickets();

    }
}
