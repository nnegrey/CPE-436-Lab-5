package edu.calpoly.android.lab3;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class JokeView extends RecyclerView.ViewHolder implements RadioGroup.OnCheckedChangeListener {

	/** Radio buttons for liking or disliking a joke. */
	private RadioButton m_vwLikeButton;
	private RadioButton m_vwDislikeButton;
	
	/** The container for the radio buttons. */
	private RadioGroup m_vwLikeGroup;

	/** Displays the joke text. */
	private TextView m_vwJokeText;
	
	/** The data version of this View, containing the joke's information. */
	private Joke m_joke;


	public JokeView(View itemView) {
		super(itemView);

		m_vwLikeButton = (RadioButton) itemView.findViewById(R.id.likeButton);
		m_vwDislikeButton = (RadioButton) itemView.findViewById(R.id.dislikeButton);
		m_vwLikeGroup = (RadioGroup) itemView.findViewById(R.id.ratingRadioGroup);
		m_vwJokeText = (TextView) itemView.findViewById(R.id.jokeTextView);
		m_vwLikeGroup.setOnCheckedChangeListener(this);
	}

	public void bind(Joke joke) {
		setJoke(joke);
	}

	/**
	 * Mutator method for changing the Joke object this View displays. This View
	 * will be updated to display the correct contents of the new Joke.
	 * 
	 * @param joke
	 *            The Joke object which this View will display.
	 */
	public void setJoke(Joke joke) {
		m_joke = joke;
        Log.d(joke.getJoke(), "setJoke: " + joke.getRating());
		m_vwJokeText.setText(m_joke.getJoke());

		if (m_joke.getRating() == Joke.LIKE) {
			m_vwLikeButton.setChecked(true);
		}
		else if (m_joke.getRating() == Joke.DISLIKE) {
			m_vwDislikeButton.setChecked(true);
		}
		else {
			m_vwLikeGroup.clearCheck();
		}
	}

	public Joke getJoke() {
		return m_joke;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (m_vwLikeButton.isChecked()) {
            m_joke.setRating(Joke.LIKE);
        }
        else if (m_vwDislikeButton.isChecked()) {
            m_joke.setRating(Joke.DISLIKE);
        }
        else {
            m_joke.setRating(Joke.UNRATED);
        }
	}
}
