package edu.calpoly.android.lab3;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdvancedJokeList extends AppCompatActivity {

    /** Contains the name of the Author for the jokes. */
    protected String m_strAuthorName;

    /** Contains the list of Jokes the Activity will present to the user. */
    protected ArrayList<Joke> m_arrJokeList;

    /** Contains the list of filtered Jokes the Activity will present to the user. */
    protected ArrayList<Joke> m_arrFilteredJokeList;

    /** Adapter used to bind an AdapterView to List of Jokes. */
    protected JokeListAdapter m_jokeAdapter;

    /** ViewGroup used for maintaining a list of Views that each display Jokes. */
    protected RecyclerView m_vwJokeLayout;

    /** EditText used for entering text for a new Joke to be added to m_arrJokeList. */
    protected EditText m_vwJokeEditText;

    /** Button used for creating and adding a new Joke to m_arrJokeList using the
     *  text entered in m_vwJokeEditText. */
    protected Button m_vwJokeButton;

    /** Menu used for filtering Jokes. */
    protected Menu m_vwMenu;

    /** Background Color values used for alternating between light and dark rows
     *  of Jokes. Add a third for text color if necessary. */
    protected int m_nDarkColor;
    protected int m_nLightColor;
    protected int m_nTextColor;

    protected int m_filter;

    private ActionMode m_actionMode;
    private ActionMode.Callback m_callBack;
    private int m_postition;

    /**
     * Context-Menu MenuItem IDs.
     * IMPORTANT: You must use these when creating your MenuItems or the tests
     * used to grade your submission will fail. These are commented out for now.
     */
    //protected static final int FILTER = Menu.FIRST;
    //protected static final int FILTER_LIKE = SubMenu.FIRST;
    //protected static final int FILTER_DISLIKE = SubMenu.FIRST + 1;
    //protected static final int FILTER_UNRATED = SubMenu.FIRST + 2;
    //protected static final int FILTER_SHOW_ALL = SubMenu.FIRST + 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced);
        // TODO
        Resources resource = this.getResources();
        m_nDarkColor = resource.getColor(R.color.dark);
        m_nLightColor = resource.getColor(R.color.light);
        m_nTextColor = resource.getColor(R.color.text);
        m_strAuthorName = resource.getString(R.string.author_name);
        m_arrJokeList = new ArrayList<>();
        m_arrFilteredJokeList = new ArrayList<>();
        m_filter = -1;
        m_jokeAdapter = new JokeListAdapter(m_arrFilteredJokeList);

        m_vwJokeLayout = (RecyclerView) findViewById(R.id.jokeListViewGroup);
        m_vwJokeLayout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState != null) {
            m_arrJokeList = savedInstanceState.getParcelableArrayList("JOKES");
            setFilteredJokeList(-1);
        }

        m_vwJokeLayout.setAdapter(m_jokeAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int index = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(AdvancedJokeList.this, "LEFT", Toast.LENGTH_SHORT).show();
                    JokeView jokeView = (JokeView) viewHolder;
                    Joke joke = jokeView.getJoke();

                    AsyncPostJokeTask apjt = new AsyncPostJokeTask();
                    apjt.execute(joke.getJoke(), joke.getAuthor());

                    m_jokeAdapter.notifyItemChanged(index);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    m_postition = index;
                    deleteJoke();
                    m_jokeAdapter.notifyItemRemoved(index);
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(m_vwJokeLayout);
        m_vwJokeEditText = (EditText) findViewById(R.id.newJokeEditText);
        m_vwJokeButton = (Button) findViewById(R.id.addJokeButton);

        initLayout();
        initAddJokeListeners();

//		for (String joke : resource.getStringArray(R.array.jokeList)) {
//			addJoke(joke);
//		}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("JOKES", m_arrJokeList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        m_vwMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                return true;
            case R.id.submenu_like:
                setFilteredJokeList(Joke.LIKE);
                m_filter = Joke.LIKE;
                return true;
            case R.id.submenu_dislike:
                setFilteredJokeList(Joke.DISLIKE);
                m_filter = Joke.DISLIKE;
                return true;
            case R.id.submenu_unrated:
                setFilteredJokeList(Joke.UNRATED);
                m_filter = Joke.UNRATED;
                return true;
            case R.id.submenu_show_all:
                setFilteredJokeList(-1);
                m_filter = -1;
                return true;
            case R.id.submenu_download_all:
                Toast.makeText(AdvancedJokeList.this, "Downloading Jokes", Toast.LENGTH_SHORT).show();
                AsyncGetJokeTask agjt = new AsyncGetJokeTask();
                agjt.execute();
                setFilteredJokeList(-1);
                m_filter = -1;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setFilteredJokeList(int filter) {
        m_arrFilteredJokeList.clear();
        for (int i = 0; i < m_arrJokeList.size(); i++) {
            if (m_arrJokeList.get(i).getRating() == filter || filter == -1) {
                m_arrFilteredJokeList.add(m_arrJokeList.get(i));
            }
        }
        m_jokeAdapter.notifyDataSetChanged();
    }

    /**
     * Method is used to encapsulate the code that initializes and sets the
     * Layout for this Activity.
     */
    protected void initLayout() {
        m_callBack = new ActionMode.Callback() {

            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.actionmenu, menu);
                return true;
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_remove:
                        deleteJoke();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                m_actionMode = null;
            }
        };
    }

    /**
     * Method is used to encapsulate the code that initializes and sets the
     * Event Listeners which will respond to requests to "Add" a new Joke to the
     * list.
     */
    protected void initAddJokeListeners() {
        m_vwJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(m_vwJokeEditText.getWindowToken(), 0);

                String jokeText = m_vwJokeEditText.getText().toString();
                if (!jokeText.isEmpty()) {
                    m_vwJokeEditText.setText("");
                    addJoke(jokeText);
                }
            }
        });

        m_vwJokeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(m_vwJokeEditText.getWindowToken(), 0);

                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    String jokeText = m_vwJokeEditText.getText().toString();
                    if (!jokeText.isEmpty()) {
                        m_vwJokeEditText.setText("");
                        addJoke(jokeText);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Method used for encapsulating the logic necessary to properly add a new
     * Joke to m_arrJokeList, and display it on screen.
     *
     * @param jokeString
     *            The Joke to add to list of Jokes.
     */
    protected void addJoke(String jokeString) {
        Joke joke = new Joke(jokeString, m_strAuthorName);

        if (!m_arrJokeList.contains(joke)) {
            this.m_arrJokeList.add(joke);
            m_arrFilteredJokeList.add(joke);
            m_jokeAdapter.notifyDataSetChanged();
        }
    }

    protected void deleteJoke() {
        Joke j = m_arrFilteredJokeList.get(m_postition);
        m_arrFilteredJokeList.remove(m_postition);
        for (int i = 0; i < m_arrJokeList.size(); i++) {
            if (j.equals(m_arrJokeList.get(i))) {
                m_arrJokeList.remove(i);
                i = m_arrJokeList.size();
            }
        }

        m_jokeAdapter.notifyDataSetChanged();
    }

    public class AsyncGetJokeTask extends AsyncTask<String, Void, Boolean> {
        public ArrayList<String> jokes;
        @Override
        protected Boolean doInBackground(String... params) {
            jokes = new ArrayList<>();
            try {
                URL url = new URL("http://simexusa.com/aac/getAllJokes.php");
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String joke;
                while ((joke = reader.readLine()) != null) {
                    if (!jokes.contains(joke) && joke != "") {
                        jokes.add(joke);
                    }
                }
                reader.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Toast.makeText(AdvancedJokeList.this, "Finished Downloading Jokes", Toast.LENGTH_SHORT).show();
            for (String joke : jokes) {
                addJoke(joke);
            }
        }
    }

    public class AsyncPostJokeTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("joke", params[0])
                    .add("author", params[1])
                    .build();
            Request request = new Request.Builder()
                    .url("http://simexusa.com/aac/addOneJoke.php")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}