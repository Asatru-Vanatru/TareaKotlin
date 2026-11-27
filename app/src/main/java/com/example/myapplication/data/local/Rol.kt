package com.example.myapplication.data.local

/** Roles de usuario. CLIENTE es el rol por defecto. */
enum class Rol(val etiqueta: String) {
    CLIENTE("Cliente"),
    TECNICO("Técnico"),
    ADMIN("Administrador");

    /** El menu de cliente es la base; estas opciones se suman segun el rol. */
    val vePanelTecnico: Boolean get() = this == TECNICO || this == ADMIN
    val administraRoles: Boolean get() = this == ADMIN
}
