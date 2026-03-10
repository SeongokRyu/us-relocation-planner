package com.seongokryu.relocationplanner.ui.screens.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.repository.ContactRepository
import com.seongokryu.relocationplanner.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel
    @Inject
    constructor(
        private val repository: ContactRepository,
    ) : ViewModel() {
        val contacts: StateFlow<List<Contact>> =
            repository.getAllContacts()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun addContact(contact: Contact) {
            viewModelScope.launch {
                repository.addContact(contact)
            }
        }

        fun deleteContact(contact: Contact) {
            viewModelScope.launch {
                repository.deleteContact(contact)
            }
        }
    }
