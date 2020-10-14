package com.theant.error.todo

class TodoNotExistException(todoId: Int) : Exception("Cannot find TODO with id is $todoId!")
