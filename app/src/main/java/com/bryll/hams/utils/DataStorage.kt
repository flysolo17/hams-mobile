package com.bryll.hams.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorage(private val dataStore: DataStore<Preferences>) {
    // At the top level of your kotlin file:


    companion object {
        val STRING_KEY = stringPreferencesKey("hams.secret.key")
    }

    // Function to store the string
    suspend fun saveToken(value: String) {
        dataStore.edit { preferences ->
            preferences[STRING_KEY] = value
        }
    }

    // Function to retrieve the string
    val getToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[STRING_KEY]
        }
    // Function to delete the stored string
    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(STRING_KEY)
        }
    }
}
