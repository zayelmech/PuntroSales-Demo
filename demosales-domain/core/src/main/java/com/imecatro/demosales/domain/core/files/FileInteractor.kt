package com.imecatro.demosales.domain.core.files

import java.io.File

interface FileInteractor {

    fun writeCsvToTickets(filename: String, rows: List<List<String>>): File
}