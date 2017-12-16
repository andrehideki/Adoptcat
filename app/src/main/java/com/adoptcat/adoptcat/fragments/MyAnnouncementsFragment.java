package com.adoptcat.adoptcat.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.adapters.AnouncementArrayAdapter;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.Announcement;
import com.adoptcat.adoptcat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MyAnnouncementsFragment extends Fragment implements ChildEventListener, AdapterView.OnItemClickListener{

    private ListView announcementsListView;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private User user;
    private AnouncementArrayAdapter adapter;
    private ArrayList<Announcement> announcements;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_announcements, container, false);
        user = User.getInstance();
        auth = Connection.getFirebaseAuth();
        databaseReference = Connection.getAnnouncementsDatabaseReference();
        databaseReference.addChildEventListener( this );
        announcements = new ArrayList<>();
        announcementsListView = (ListView) view.findViewById(R.id.announcementsListView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AnouncementArrayAdapter( getActivity(), this.announcements );
        announcementsListView.setOnItemClickListener( this );
        announcementsListView.setAdapter( adapter );
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Announcement a = dataSnapshot.getValue( Announcement.class );
        String uuid = a.getUserUUID();
        if(uuid.equals(user.getUUID())) {
            announcements.add( a );
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        announcements.remove( dataSnapshot.getValue( Announcement.class ) );
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Announcement announcement = announcements.get(position);
        final CharSequence[] options = {getString(R.string.dialog_delete_announcement_option),
                getString(R.string.dialog_edit_announcement_option), getString(R.string.dialog_cancel_option)};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder( getActivity() );
        alertBuilder.setTitle( getString(R.string.dialog_announcement_title) );
        alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://Delete
                        deleteAnnouncement( announcement );
                        dialog.dismiss();
                        break;
                    case 1://Edit
                        editAnnouncement( announcement );
                        dialog.dismiss();
                        break;
                    case 2://cancel
                        dialog.cancel();
                }
            }
        });
        alertBuilder.show();

    }

    private void editAnnouncement(Announcement announcement) {
        Bundle bundle = new Bundle();
        RegisterCatFragment fragment = new RegisterCatFragment();
        bundle.putSerializable("announcement", announcement);
        fragment.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace( R.id.main_content, fragment);
        ft.commit();
    }

    private void deleteAnnouncement( Announcement a ) {
        DatabaseReference databaseReference = Connection.getAnnouncementsDatabaseReference().child(a.getId());
        databaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                showMessage(getString(R.string.myannounces_annoucement_deleted));
            }
        });
    }


    private void showMessage( String msg ) {
        Toast.makeText( getActivity(), msg, Toast.LENGTH_LONG ).show();
    }
}
