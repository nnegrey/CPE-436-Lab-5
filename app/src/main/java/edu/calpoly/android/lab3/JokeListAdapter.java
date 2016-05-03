package edu.calpoly.android.lab3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class binds the visual JokeViews and the data behind them (Jokes).
 */
public class JokeListAdapter extends RecyclerView.Adapter<JokeView> {

	/** The application Context in which this JokeListAdapter is being used. */
	private Context m_context;

	/** The data set to which this JokeListAdapter is bound. */
	private List<Joke> m_jokeList;

//	/**
//	 * Parameterized constructor that takes in the application Context in which
//	 * it is being used and the Collection of Joke objects to which it is bound.
//	 * m_nSelectedPosition will be initialized to Adapter.NO_SELECTION.
//	 *
//	 * @param context
//	 *            The application Context in which this JokeListAdapter is being
//	 *            used.
//	 *
//	 * @param jokeList
//	 *            The Collection of Joke objects to which this JokeListAdapter
//	 *            is bound.
//	 */
//	public JokeListAdapter(Context context, List<Joke> jokeList) {
//		m_context = context;
//		m_jokeList = jokeList;
//	}
//
//    @Override
//    public int getCount() {
//        return m_jokeList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return m_jokeList.get(position);
//    }
    public JokeListAdapter(ArrayList<Joke> jokes) {
        this.m_jokeList = jokes;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.joke_view;
    }

    @Override
    public JokeView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JokeView(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(JokeView holder, int position) {
        holder.bind(m_jokeList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return m_jokeList.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = new JokeView(m_context, m_jokeList.get(position));
//        }
//        else {
//            ((JokeView) convertView).setJoke(m_jokeList.get(position));
//        }
//
//        return convertView;
//    }
}
