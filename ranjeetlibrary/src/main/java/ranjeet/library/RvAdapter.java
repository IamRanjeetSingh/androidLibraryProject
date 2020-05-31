package ranjeet.library;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class RvAdapter<T> extends RecyclerView.Adapter {

    private List<T> dataSet;
    private List<WeakReference<RecyclerView.ViewHolder>> holders;

    public RvAdapter(List<T> dataSet){
        this.holders = new ArrayList<>();
        this.dataSet = dataSet;
    }

    public final void setDataSet(List<T> newDataSet){
        List<T> oldDataSet = this.dataSet;
        this.dataSet.clear();
        this.dataSet.addAll(newDataSet);
        dataSetChanged(oldDataSet, newDataSet);
    }

    public final void setDataSet(List<T> newDataSet, DiffUtil.Callback diffUtilCallback){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        diffResult.dispatchUpdatesTo(this);
        this.dataSet.clear();
        this.dataSet.addAll(newDataSet);
    }

    public final List<T> getDataSet(){
        return new ArrayList<>(this.dataSet);
    }

    public final void addData(T data){
        this.dataSet.add(data);
        notifyItemInserted(this.dataSet.size()-1);
    }

    public final void addData(int position, T data){
        this.dataSet.add(position, data);
        notifyItemInserted(position);
    }

    public final void deleteData(T data){
        int position = this.dataSet.indexOf(data);
        this.dataSet.remove(data);
        notifyItemRemoved(position);
    }

    public final void deleteData(int position){
        this.dataSet.remove(position);
        notifyItemRemoved(position);
    }

    public final void updateData(int position, T newData){
        this.dataSet.set(position, newData);
        notifyItemChanged(position);
    }

    private void dataSetChanged(List<T> oldDataSet, List<T> newDataSet){
        int oldStart = getOldStart();
        int oldEnd = getOldEnd();

        int newStart, newEnd;
        if(newDataSet.size()<oldEnd+1){
            newEnd = newDataSet.size()-1;
            newStart = Math.max(newEnd - (oldEnd-oldStart),0);
        } else {
            newStart = oldStart;
            newEnd = oldEnd;
        }

        for(int oldIndex=oldStart,newIndex=newStart; oldIndex<oldEnd; oldIndex++,newIndex++){
            if(newIndex<=newEnd){
                if(!newDataSet.get(newIndex).equals(oldDataSet.get(oldIndex))){
                    notifyItemChanged(newIndex);
                }
            } else{
                notifyItemRemoved(oldIndex);
            }
        }
    }

    private int getOldStart(){
        int oldStart = Integer.MAX_VALUE;
        for(WeakReference<RecyclerView.ViewHolder> holder:holders){
            if(holder!=null && holder.get().getAdapterPosition()!=RecyclerView.NO_POSITION && holder.get().getAdapterPosition()<oldStart){
                oldStart = holder.get().getAdapterPosition();
            }
        }
        return oldStart;
    }

    private int getOldEnd(){
        int oldEnd = Integer.MIN_VALUE;
        for(WeakReference<RecyclerView.ViewHolder> holder:holders){
            if(holder!=null && holder.get().getAdapterPosition()!=RecyclerView.NO_POSITION && holder.get().getAdapterPosition()>oldEnd){
                oldEnd = holder.get().getAdapterPosition();
            }
        }
        return oldEnd;
    }
    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = onCreateRvViewHolder(parent, viewType);
        this.holders.add(new WeakReference<>(holder));
        return holder;
    }

    public abstract RecyclerView.ViewHolder onCreateRvViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    @Override
    public abstract int getItemCount();
}
