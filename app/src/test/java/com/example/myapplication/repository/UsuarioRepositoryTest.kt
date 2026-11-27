package com.example.myapplication.repository

import com.example.myapplication.data.local.Rol
import com.example.myapplication.data.local.USUARIOS_BASE
import com.example.myapplication.data.local.UsuarioDao
import com.example.myapplication.data.local.UsuarioEntity
import com.example.myapplication.data.repository.UsuarioRepository
import com.example.myapplication.data.repository.puedeCambiarRol
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class UsuarioRepositoryTest : StringSpec({

    // Mismos usuarios base, con un id como el que asignaria la base.
    val usuarios = USUARIOS_BASE.mapIndexed { i, u -> u.copy(id = i + 1L) }
    fun usuario(nombre: String): UsuarioEntity = usuarios.first { it.nombre == nombre }
    val administradores = listOf("Amanda", "Maira", "Benjamin")

    fun dao(): UsuarioDao {
        val dao = mockk<UsuarioDao>(relaxed = true)
        coEvery { dao.buscarPorNombre(any()) } answers {
            usuarios.firstOrNull { it.nombre == firstArg<String>() }
        }
        coEvery { dao.obtenerPorId(any()) } answers {
            usuarios.firstOrNull { it.id == firstArg<Long>() }
        }
        return dao
    }

    "los usuarios base tienen el rol inicial esperado" {
        usuarios.filter { it.rol == Rol.ADMIN && it.protegido }
            .map { it.nombre } shouldContainExactlyInAnyOrder administradores
        usuario("Tecnico").rol shouldBe Rol.TECNICO
        usuario("Cliente").rol shouldBe Rol.CLIENTE
        usuario("Tester").rol shouldBe Rol.CLIENTE
    }

    "el rol por defecto es cliente" {
        UsuarioEntity(nombre = "Nuevo", clave = "Nuevo").rol shouldBe Rol.CLIENTE
    }

    "cada menu suma opciones sobre el de cliente" {
        Rol.CLIENTE.vePanelTecnico shouldBe false
        Rol.CLIENTE.administraRoles shouldBe false
        Rol.TECNICO.vePanelTecnico shouldBe true
        Rol.TECNICO.administraRoles shouldBe false
        Rol.ADMIN.vePanelTecnico shouldBe true
        Rol.ADMIN.administraRoles shouldBe true
    }

    "autenticar() acepta credenciales correctas y rechaza las demas" {
        val repo = UsuarioRepository(dao())
        val tester = usuario("Tester")

        repo.autenticar(" ${tester.nombre} ", tester.clave) shouldBe tester
        repo.autenticar(tester.nombre, "otra") shouldBe null
        repo.autenticar("Nadie", tester.clave) shouldBe null
    }

    "cada administrador base puede cambiar a los demas usuarios" {
        administradores.forEach { admin ->
            listOf("Tecnico", "Cliente", "Tester").forEach { nombre ->
                puedeCambiarRol(usuario(admin), usuario(nombre), Rol.TECNICO) shouldBe true
                puedeCambiarRol(usuario(admin), usuario(nombre), Rol.CLIENTE) shouldBe true
            }
        }
    }

    "los administradores base no cambian roles entre ellos ni el propio" {
        val dao = dao()
        val repo = UsuarioRepository(dao)

        administradores.forEach { actor ->
            administradores.forEach { objetivo ->
                repo.cambiarRol(usuario(actor).id, usuario(objetivo).id, Rol.CLIENTE) shouldBe false
            }
        }
        coVerify(exactly = 0) { dao.actualizarRol(any(), any()) }
    }

    "cliente y tecnico no pueden cambiar roles" {
        val repo = UsuarioRepository(dao())

        listOf("Cliente", "Tecnico", "Tester").forEach { actor ->
            repo.cambiarRol(usuario(actor).id, usuario("Tester").id, Rol.TECNICO) shouldBe false
        }
    }

    "no se pueden nombrar administradores nuevos" {
        puedeCambiarRol(usuario("Amanda"), usuario("Tester"), Rol.ADMIN) shouldBe false
    }

    "un cambio permitido se guarda" {
        val dao = dao()

        UsuarioRepository(dao)
            .cambiarRol(usuario("Maira").id, usuario("Tester").id, Rol.TECNICO) shouldBe true

        coVerify(exactly = 1) { dao.actualizarRol(usuario("Tester").id, Rol.TECNICO) }
    }
})
