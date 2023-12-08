package com.sp.a0miniproj.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.sp.a0miniproj.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.sp.a0miniproj.Utility;
import com.sp.a0miniproj.databinding.ActivityNotesBinding;
import com.sp.a0miniproj.drawerbase;


public class Notes extends drawerbase {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;
    ActivityNotesBinding activityNotesBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_notes);

        activityNotesBinding=ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(activityNotesBinding.getRoot());
        allocatedtitle("Notes");

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn);

        addNoteBtn.setOnClickListener((v)-> startActivity(new Intent(Notes.this, notedetail.class)) );
     //  menuBtn.setOnClickListener((v)->showMenu() );
        //setupRecyclerView();
        Query query  = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options,this);
        recyclerView.setAdapter(noteAdapter);
    }

   /* void showMenu(){
        PopupMenu popupMenu  = new PopupMenu(Notes.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Notes.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailsmenu, menu);
        return true;
    }
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.logout){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Notes.this, LoginActivity.class));
        finish();
        return true;
    }
    return false;

    }

      /*  void setupRecyclerView(){
        Query query  = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options,this);
        recyclerView.setAdapter(noteAdapter);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}