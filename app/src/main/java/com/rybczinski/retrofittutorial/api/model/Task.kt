package com.rybczinski.retrofittutorial.api.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "task")
class Task(
    @Element(name = "id") private val id: Long,
    @Element(name = "title") private val title: String,
    @Element(name = "description") private val description: String,
    @Element(name = "language") private val language: String
)