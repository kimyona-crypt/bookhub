package com.nikita.bookhub.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nikita.bookhub.model.Book

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)
    @Query ("select * from Book")
    fun getAllBooks(): List<BookEntity>

    @Query(     "select * from Book where BookId=:BookId")
    fun getBookId(BookId: String): BookEntity
}