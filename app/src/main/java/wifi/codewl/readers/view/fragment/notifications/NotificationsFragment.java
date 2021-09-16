package wifi.codewl.readers.view.fragment.notifications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.notifications.NotificationsUploadFile;
import wifi.codewl.readers.model.progress.ProgressNotifications;
import wifi.codewl.readers.presenter.NotificationsAdapter;

public class NotificationsFragment extends Rocket {

    private Context context;

    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;

    private NotificationsViewModel notificationsViewModel;


    {
        fragmentName = "navigation_notifications";
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationsRecyclerView = root.findViewById(R.id.recyclerview_notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<Model> list = new ArrayList<>();
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new NotificationsUploadFile());
        list.add(new ProgressNotifications());

        notificationsAdapter = new NotificationsAdapter(context,list);
        notificationsRecyclerView.setAdapter(notificationsAdapter);

        return root;
    }

    public static NotificationsFragment newInstance(){
        NotificationsFragment notificationsFragment= new NotificationsFragment();
        Bundle bundle = new Bundle();
        notificationsFragment.setArguments(bundle);
        return notificationsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}