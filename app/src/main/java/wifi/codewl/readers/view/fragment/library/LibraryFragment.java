package wifi.codewl.readers.view.fragment.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import wifi.codewl.readers.R;
import wifi.codewl.readers.model.Model;
import wifi.codewl.readers.model.Rocket;
import wifi.codewl.readers.model.library.RecentHistory;
import wifi.codewl.readers.presenter.RecentHistoryAdapter;
import wifi.codewl.readers.view.activity.HistoryActivity;
import wifi.codewl.readers.view.activity.MainActivity;
import wifi.codewl.readers.view.activity.SettingActivity;
import wifi.codewl.readers.view.activity.UploadFileActivity;
import wifi.codewl.readers.view.activity.WatchLaterActivity;
import wifi.codewl.readers.view.custom.CustomSwipeToRefresh;

public class LibraryFragment extends Rocket {

    private CustomSwipeToRefresh librarySwipeRefresh;
    private RecyclerView historyRecyclerView;
    private RecentHistoryAdapter recentHistoryAdapter;
    private LinearLayout history, yourChannel, uploadFile, watchLater, setting;
    private MainActivity context;
    List<Model> list;

    private LibraryViewModel libraryViewModel;

    {
        fragmentName = "navigation_library";
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_library, container, false);

        librarySwipeRefresh = root.findViewById(R.id.swipeRefresh_library);
        librarySwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, context.getTheme()));
        librarySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        librarySwipeRefresh.post(new Runnable() {
                            @Override
                            public void run() {
                                list.clear();
                                recentHistoryAdapter.getData();
                                librarySwipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        history = root.findViewById(R.id.history_library);
        yourChannel = root.findViewById(R.id.your_channel_library);
        uploadFile = root.findViewById(R.id.upload_file_library);
        watchLater = root.findViewById(R.id.watch_later_library);
        setting = root.findViewById(R.id.setting_library);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, HistoryActivity.class));
            }
        });
        yourChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.goTo_My_Channel(v);
            }
        });
        uploadFile.setOnClickListener(v -> {
            context.goToUploadFile(v);
        });

        watchLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WatchLaterActivity.class));
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SettingActivity.class));
            }
        });

        historyRecyclerView = root.findViewById(R.id.recyclerview_history);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

      list = new ArrayList<>();

        recentHistoryAdapter = new RecentHistoryAdapter(context, list);
        recentHistoryAdapter.getData();
        historyRecyclerView.setAdapter(recentHistoryAdapter);


        return root;
    }


    public static LibraryFragment newInstance() {
        LibraryFragment libraryFragment = new LibraryFragment();
        Bundle bundle = new Bundle();
        libraryFragment.setArguments(bundle);
        return libraryFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = (MainActivity) context;
    }


}