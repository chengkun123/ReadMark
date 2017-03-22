package com.mycompany.readmark.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.readmark.R;
import com.mycompany.readmark.books.BooksBean;

/**
 * Created by Lenovo on 2017/3/14.
 */
public class BookDetailActivity extends AppCompatActivity{
    private BookDetailFragment mBookDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        BooksBean book = (BooksBean) getIntent().getSerializableExtra("book");

        mBookDetailFragment = BookDetailFragment.newInstance(book);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_detail, mBookDetailFragment)
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
