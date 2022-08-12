package com.purnendu.testingwithandroid.feature_note.presentation.notes


import com.google.common.truth.Truth.assertThat
import com.purnendu.testingwithandroid.feature_note.domain.model.Note
import com.purnendu.testingwithandroid.feature_note.domain.repository.FakeNoteRepository
import com.purnendu.testingwithandroid.feature_note.domain.use_case.*
import com.purnendu.testingwithandroid.feature_note.domain.util.NoteOrder
import com.purnendu.testingwithandroid.feature_note.domain.util.OrderType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class NotesViewModelTest {

    private lateinit var notesViewModel: NotesViewModel
    private lateinit var noteUseCases: NoteUseCases
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private val notesToInsert = mutableListOf<Note>()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
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
            notesToInsert.forEach { fakeNoteRepository.insertNote(it)
            }
        }

        noteUseCases = NoteUseCases(
            getNote = GetNote(fakeNoteRepository),
            deleteNote = DeleteNote(fakeNoteRepository),
            addNote = AddNote(fakeNoteRepository),
            getNotes = GetNotes(fakeNoteRepository)
        )

        notesViewModel = NotesViewModel(noteUseCases)
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
        for(i in currentNoteList.indices) {
            assertThat(currentNoteList[25-i].title.compareTo(notesToInsert[i].title)).isEqualTo(0)
            assertThat(currentNoteList[25-i].content.compareTo(notesToInsert[i].content)).isEqualTo(0)
            assertThat(currentNoteList[25-i].color == notesToInsert[i].color).isTrue()
            assertThat(currentNoteList[25-i].timestamp == notesToInsert[i].timestamp).isTrue()
        }
    }

    @Test
    fun restoringNoteTest()
    {
        val deletingNote=Note("a","a",0L,0)
        notesViewModel.onEvent(NotesEvent.DeleteNote(deletingNote))
        notesViewModel.onEvent(NotesEvent.RestoreNote)
        val currentNoteList=notesViewModel.state.value.notes
        assertThat(currentNoteList.contains(deletingNote)).isTrue()
    }


}