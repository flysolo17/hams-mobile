package com.bryll.hams.repository.datastore

import kotlinx.coroutines.flow.Flow

interface DataStore {
    suspend fun saveToken(value: String)
    suspend fun deleteToken()
     suspend fun getToken() : String?
}