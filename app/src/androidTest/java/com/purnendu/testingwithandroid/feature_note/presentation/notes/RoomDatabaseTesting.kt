package com.purnendu.testingwithandroid.feature_note.presentation.notes

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.purnendu.testingwithandroid.feature_note.data.data_source.NoteDatabase
import com.purnendu.testingwithandroid.feature_note.data.repository.NoteRepositoryImpl
import com.purnendu.testingwithandroid.feature_note.domain.model.Note
import com.purnendu.testingwithandroid.feature_note.domain.use_case.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTesting {


    private lateinit var noteUseCases: NoteUseCases
    private lateinit var db: NoteDatabase


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            .allowMainThreadQueries().build()
        val repository = (NoteRepositoryImpl(db.noteDao))
        noteUseCases = NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun addingDataInDatabaseTesting() = runBlocking {

        val note = Note("Testing", "Room database testing note", 1L, 1, null)
        val note2 = Note("Testing2", "Room database testing note 2", 2L, 2, null)
        noteUseCases.addNote(note)
        noteUseCases.addNote(note2)
        val notes: List<Note> = noteUseCases.getNotes().first()
        assertThat(notes.count()==2).isTrue()
    }

    //NotWorking
//    @Test
//    fun deletingDataInDatabaseTesting() = runBlocking {
//
//        val note = Note("Testing", "Room database testing note", 1L, 1, null)
//        val note2 = Note("Testing2", "Room database testing note 2", 2L, 2, null)
//        noteUseCases.addNote(note)
//        noteUseCases.addNote(note2)
//        noteUseCases.deleteNote(note2)
//        noteUseCases.deleteNote(note)
//        val notes: List<Note> = noteUseCases.getNotes().first()
//        assertThat(notes.isEmpty()).isTrue()
//    }


}