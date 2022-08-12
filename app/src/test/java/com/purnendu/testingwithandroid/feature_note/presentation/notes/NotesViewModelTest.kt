package com.purnendu.testingwithandroid.feature_note.presentation.notes


import com.google.common.truth.Truth.assertThat
import com.purnendu.testingwithandroid.feature_note.domain.model.Note
import com.purnendu.testingwithandroid.feature_note.domain.repository.FakeNoteRepository
import com.purnendu.testingwithandroid.feature_note.domain.use_case.*
import com.purnendu.testingwithandroid.feature_note.domain.util.NoteOrder
import com.purnendu.testingwithandroid.feature_note.domain.util.OrderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class NotesViewModelTest {

    private lateinit var notesViewModel: NotesViewModel
    private lateinit var noteUseCases: NoteUseCases
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private val dispatcher = StandardTestDispatcher()
    private val notesToInsert = mutableListOf<Note>()



    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fakeNoteRepository = FakeNoteRepository()


        ('a'..'z').forEachIndexed { index, c ->
            notesToInsert.add(
                Note(
                    title = c.toString(),
                    content = c.toString(),
                    timestamp = index.toLong(),
                    color = index
                )
            )
        }
        notesToInsert.shuffle()
        runBlocking {
            notesToInsert.forEach { fakeNoteRepository.insertNote(it) }
        }

        noteUseCases = NoteUseCases(
            getNote = GetNote(fakeNoteRepository),
            deleteNote = DeleteNote(fakeNoteRepository),
            addNote = AddNote(fakeNoteRepository),
            getNotes = GetNotes(fakeNoteRepository)
        )

        notesViewModel = NotesViewModel(noteUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun orderingNoteTest()
    {
        notesViewModel.onEvent(NotesEvent.Order(NoteOrder.Title(OrderType.Descending)))
        val currentNoteList=notesViewModel.state.value.notes
        for(i in 0..currentNoteList.size - 2) {
            assertThat(currentNoteList[i].title).isGreaterThan(currentNoteList[i+1].title)
        }
    }

    @Test
    fun deletingNoteTest()
    {
        val deletingNote=Note("m","m",13L,13)
        notesViewModel.onEvent(NotesEvent.DeleteNote(deletingNote))
        val currentNoteList=notesViewModel.state.value.notes
        assertThat(currentNoteList.contains(deletingNote)).isFalse()
    }

    @Test
    fun gettingAllNotesTest()
    {
        val currentNoteList=notesViewModel.state.value.notes
       // print(currentNoteList.toString())
        for(i in currentNoteList.indices) {
            assertThat(currentNoteList[i].title == notesToInsert[i].title).isTrue()
            assertThat(currentNoteList[i].content == notesToInsert[i].content).isTrue()
            assertThat(currentNoteList[i].timestamp == notesToInsert[i].timestamp).isTrue()
            assertThat(currentNoteList[i].color == notesToInsert[i].color).isTrue()
        }
    }

//    @Test
//    fun restoringNoteTest()
//    {
//        val deletingNote=Note("a","a",0L,0)
//        notesViewModel.onEvent(NotesEvent.DeleteNote(deletingNote))
//        notesViewModel.onEvent(NotesEvent.RestoreNote)
//        val currentNoteList=notesViewModel.state.value.notes
//        assertThat(currentNoteList.contains(deletingNote)).isTrue()
//    }


}