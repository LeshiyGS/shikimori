package org.shikimori.library.fragments.base.abstracts.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.shikimori.library.R;

import java.util.ArrayList;
import java.util.List;

public abstract class HeaderRecyclerAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int layout;
    protected LayoutInflater inflater;
    //our items
    List<T> items = new ArrayList<>();
    //headers
    List<View> headers = new ArrayList<>();
    //footers
    List<View> footers = new ArrayList<>();

    public static final int TYPE_HEADER = 111;
    public static final int TYPE_FOOTER = 222;
    public static final int TYPE_ITEM = 333;
    private OnItemClickRecycleListener<T> onItemClickListener;

    public HeaderRecyclerAdapter(Context context, List<T> items, int layout){
        this.context = context;
        this.items = items;
        this.layout = layout;
        this.inflater = LayoutInflater.from(context);
    }

    public abstract H getViewHolder(View var1);

    public abstract void setListeners(H holder);

    protected int getLayout(int viewType) {
        return this.layout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        //if our position is one of our items (this comes from getItemViewType(int position) below)
        if(type == TYPE_HEADER || type == TYPE_FOOTER){
            //create a new framelayout, or inflate from a resource
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new HeaderFooterViewHolder(frameLayout);
        } else if (type == TYPE_ITEM){
            View view = inflater.inflate(getLayout(type), viewGroup, false);
            if(onItemClickListener!=null){
                view.setOnClickListener(itemClickListener);
            }

            H h = getViewHolder(view);
            setListeners(h);
            return h;
        }

        return null;
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag(R.id.item_position);
            int fixPos = vh.getAdapterPosition() - headers.size();
            onItemClickListener.onItemClick(items.get(fixPos), fixPos);
        }
    };

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        //check what type of view our position is
        if (position < headers.size()) {
            View v = headers.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        } else if (position >= headers.size() + items.size()) {
            View v = footers.get(position - items.size() - headers.size());
            //add oru view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        } else  {
            //it's one of our items, display as required
            int fixPos = position - headers.size();
            T item = items.get(fixPos);
            vh.itemView.setTag(R.id.item_position, vh);
            setValues((H) vh, item, fixPos);
        }
    }

    @Override
    public int getItemCount() {
        //make sure the adapter knows to look for all our items, headers, and footers
        return headers.size() + items.size() + footers.size();
    }

    private void prepareHeaderFooter(HeaderFooterViewHolder vh, View view) {
        //empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews();
        vh.base.addView(view);
    }

    public abstract void setValues(H h, T item, int position);

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if (position < headers.size()) {
            return TYPE_HEADER;
        } else if (position >= headers.size() + items.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    //add a header to the adapter
    public void addHeader(View header) {
        if (!headers.contains(header)) {
            headers.add(header);
            //animate
            notifyItemInserted(headers.size() - 1);
        }
    }

    //remove a header from the adapter
    public void removeHeader(View header) {
        if (headers.contains(header)) {
            //animate
            notifyItemRemoved(headers.indexOf(header));
            headers.remove(header);
        }
    }


    public void removeItem(int position){
        int fixPos = position + headers.size();
        items.remove(position);
        notifyItemRemoved(fixPos);
    }

    public void removeItem(T item){
        int position = items.indexOf(item);
        if(position>=0){
            int fixPos = position + headers.size();
            items.remove(position);
            notifyItemRemoved(fixPos);
        }
    }

    public void notifyItem(int position) {
        int fixPos = position + headers.size();
        notifyItemChanged(fixPos);
    }

    public void addItem(T item){
        items.add(item);
        int position = items.indexOf(item);
        int fixPos = position + headers.size();
        notifyItemInserted(fixPos);
    }

    public void addItem(int position, T item){
        int fixPos = position + headers.size();
        items.add(position, item);
        notifyItemInserted(fixPos);
    }

    //add a footer to the adapter
    public void addFooter(View footer) {
        if (!footers.contains(footer)) {
            footers.add(footer);
            //animate
            notifyItemInserted(headers.size() + items.size() + footers.size() - 1);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer) {
        if (footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size() + items.size() + footers.indexOf(footer));
            footers.remove(footer);
        }
    }

    public void setOnItemClickListener(OnItemClickRecycleListener<T> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public Context getContext() {
        return context;
    }

    //our header/footer RecyclerView.ViewHolder is just a FrameLayout
    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
        FrameLayout base;

        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }



    public T getItem(int position){
        return items.get(position);
    }
}