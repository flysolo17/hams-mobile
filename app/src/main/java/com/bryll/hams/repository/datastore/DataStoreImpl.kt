package com.bryll.hams.repository.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreImpl(private val dataStore: androidx.datastore.core.DataStore<Preferences>) : DataStore {
    companion object {
        val STRING_KEY = stringPreferencesKey("hams.secret.key")
    }

    // Function to store the string
    override suspend fun saveToken(value: String) {
        dataStore.edit { preferences ->
            preferences[STRING_KEY] = value
        }
    }

    override suspend fun getToken(): String? {
        val token = dataStore.data.first()
        return token[STRING_KEY]
    }

    // Function to delete the stored string
    override suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}