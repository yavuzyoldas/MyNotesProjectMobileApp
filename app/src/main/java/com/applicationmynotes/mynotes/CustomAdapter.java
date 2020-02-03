package com.applicationmynotes.mynotes;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerSwipeAdapter<CustomAdapter.SimpleViewHolder> {

    ArrayList<Notes> notesArrayList;
    Context context;
    private MainActivity mA;




   public CustomAdapter(ArrayList<Notes> notesArrayList, Context context , MainActivity mA ) {
       this.notesArrayList = notesArrayList;
       this.context = context;
       this.mA = mA;

   }
    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {

        final ExpandableLinearLayout  content = holder.itemView.findViewById(R.id.content);

        final Notes note = notesArrayList.get(position);


        holder.txtTitle.setText(note.getTitle());

        holder.txtContent.setText(note.getSummary());
        holder.text.setText(note.getContent());


        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


        // soldan
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wraper));

        //sağdan
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper1));

        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });


        Button  btnDelete =  holder.itemView.findViewById(R.id.btnDeleteForNote);

        Button  btnUpdate = holder.itemView.findViewById(R.id.btnUpdateForNote);



       holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.toggle();
            }
        });

        holder.swipeLayout.getSurfaceView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showNoteDetail(note.getTitle(),note.getContent());
                return false;
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                     mA.sendDeleteNoteRequest(note.getId());
                     mItemManger.removeShownLayouts(holder.swipeLayout);
                    int curr_position =  notesArrayList.indexOf(note);
                    notesArrayList.remove(curr_position);
                    notifyItemRemoved(curr_position);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateNoteDialog(note.getId(),note.getTitle(),note.getContent());

            }
        });

        mItemManger.bindView(holder.itemView, position);

    }
    @Override
    public int getItemCount() {//Gelen verilerin boyutunu döndürür.
        return notesArrayList.size();
    }




    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    private void showNoteDetail(String editTextHeader,String editText_content)
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = layoutInflater.inflate(R.layout.note_detail,null);

        dialogBuilder.setView(dialogView);

        final TextView editHeader = dialogView.findViewById(R.id.textView_NoteTitle);

        editHeader.setText(editTextHeader);

        final TextView editContent = dialogView.findViewById(R.id.textView_NoteContent);

        editContent.setText(editText_content);

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();
    }

    private void  updateNoteDialog( final String noteId, String title, String content){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       //Layoutinflater sınıfı temel olarak XML objeleri Java sınıflarına çevirir.

        View dialogView = layoutInflater.inflate(R.layout.note_update,null);

        dialogBuilder.setView(dialogView); //dialog görünümünü oluştruduğumuz xml ile setledik.

      final  EditText editTitle = dialogView.findViewById(R.id.editTextNoteTitleUpdate);

        editTitle.setText(title);

      final  EditText editContent = dialogView.findViewById(R.id.editTextNoteContentUpdate);

        editContent.setText(content);

        Button updateButton = dialogView.findViewById(R.id.updateButton);

       final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note_Id = noteId;
                String note_title = editTitle.getText().toString().trim();
                String note_content = editContent.getText().toString().trim();

                if(TextUtils.isEmpty(note_title))
                {
                    editTitle.setError("Bu alan doldurulmalıdır!");
                    return;
                }
                if(TextUtils.isEmpty(note_content))
                {
                    editContent.setError("Bu alan doldurulmalıdır!");
                    return;
                }

                mA.sendUpdateNoteRequest(note_Id,note_title,note_content);

                mA.recreate();

                notifyDataSetChanged();

                alertDialog.dismiss();
            }
        });
    }

     class SimpleViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtContent,text;
        Button deleteBtn,updateBtn;
        public SwipeLayout swipeLayout;




        public SimpleViewHolder(@NonNull View itemView){
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            deleteBtn = itemView.findViewById(R.id.btnDeleteForNote);
            updateBtn = itemView.findViewById(R.id.btnUpdateForNote);
            text = itemView.findViewById(R.id.text11);
            swipeLayout = itemView.findViewById(R.id.swipe);
        }


    }

}
