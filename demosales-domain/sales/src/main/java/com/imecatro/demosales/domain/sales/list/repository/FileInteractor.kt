package com.imecatro.demosales.domain.sales.list.repository

import java.io.File


interface FileInteractor {

    fun writeCsvToTickets(filename: String, rows: List<List<String>>): File
}