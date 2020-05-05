package com.nikita.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.nikita.bookhub.Database.BookDatabase
import com.nikita.bookhub.Database.BookEntity
import com.nikita.bookhub.R
import com.nikita.bookhub.util.connectionmanager
import com.squareup.picasso.Picasso

import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor : TextView
    lateinit var txtBookPrice : TextView
    lateinit var txtBookRating : TextView
    lateinit var imgBookImage :ImageView
    lateinit var txtBookDesc : TextView
    lateinit var btnAddToFav : Button
    lateinit var toolbar: Toolbar
    var BookId: String? ="100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        btnAddToFav = this.findViewById(R.id.btnAddToFav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"


        if (intent != null) {
            BookId = intent.getStringExtra("book_id")

        } else {
            finish()
            Toast.makeText(this@DescriptionActivity, "some error occurred!!", Toast.LENGTH_SHORT)
                .show()
        }
        if (BookId == "100") {
            finish()
            Toast.makeText(this@DescriptionActivity, "Some error occurred", Toast.LENGTH_SHORT)
                .show()
        }
        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", BookId)
        if(connectionmanager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")

                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")
                            val bookEntity = BookEntity(
                                BookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookDesc.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                bookImageUrl
                            )
                            val checkFav = DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav = checkFav.get()
                            if(isFav){
                                btnAddToFav.text = "Remove from favorites"
                                val favcolor = ContextCompat.getColor(applicationContext,R.color.colorFavorites)
                                btnAddToFav.setBackgroundColor(favcolor)
                            }
                            else{
                                btnAddToFav.text="Add to favorites"
                                val nofavcolor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(nofavcolor)
                            }
                            btnAddToFav.setOnClickListener{
                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                    val async = DBAsyncTask(applicationContext,bookEntity,2).execute()
                                    val result = async.get()
                                    if(result) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book added to the favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Remove from favorites"
                                        val favcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFavorites
                                        )
                                        btnAddToFav.setBackgroundColor(favcolor)

                                    }
                                    else{
                                        Toast.makeText(this@DescriptionActivity,"Some error occured",Toast.LENGTH_SHORT).show()

                                    }

                                }else{
                                    val async = DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result = async.get()
                                    if (result){
                                        Toast.makeText(this@DescriptionActivity,"Book removed from favprites",Toast.LENGTH_SHORT).show()
                                        btnAddToFav.text="Add to favorites"
                                        val nofavcolor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                        btnAddToFav.setBackgroundColor(nofavcolor)
                                    }
                                    else{
                                        Toast.makeText(this@DescriptionActivity,"Some error occured",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity, "Volley error $it", Toast.LENGTH_SHORT)
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "2bc64bd10b79b8"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        }else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }
    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int): AsyncTask<Void,Void, Boolean>(){
       /*mode-1->to check if the book is in the favorites or not
         mode-2->if not add to favorites
         mode-3->remove from favorites
        */
        val db = Room.databaseBuilder(context, BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
           when(mode){
               1->{
                   val book: BookEntity? = db.bookDao().getBookId(bookEntity.BookId.toString())
                   db.close()
                   return book!=null
               }
               2->{
                   db.bookDao().insertBook(bookEntity)
                   db.close()
                   return true
               }
               3->{
                   db.bookDao().deleteBook(bookEntity)
                   db.close()
                   return true
               }
           }
           return false
        }

    }
}

