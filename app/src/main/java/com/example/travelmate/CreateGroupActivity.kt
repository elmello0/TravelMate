package com.example.travelmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var groupNameEditText: EditText
    private lateinit var groupDescriptionEditText: EditText
    private lateinit var createGroupButton: Button
    private lateinit var addMembersButton: Button
    private lateinit var cancelButton: Button
    private lateinit var saveGroupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        groupNameEditText = findViewById(R.id.etGroupName)
        groupDescriptionEditText = findViewById(R.id.etGroupDescription)
        createGroupButton = findViewById(R.id.btnCreateGroup)
        addMembersButton = findViewById(R.id.btnAddMembers)
        cancelButton = findViewById(R.id.btnCancel)
        saveGroupButton = findViewById(R.id.btnSaveGroup)

        createGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString()
            val groupDescription = groupDescriptionEditText.text.toString()

            if (groupName.isNotEmpty()) {
                Toast.makeText(this, "Grupo creado: $groupName", Toast.LENGTH_SHORT).show()
                // Aquí puedes agregar lógica para almacenar el grupo en una base de datos o lista
            } else {
                Toast.makeText(this, "Por favor, ingresa un nombre para el grupo", Toast.LENGTH_SHORT).show()
            }
        }

        addMembersButton.setOnClickListener {
            val intent = Intent(this, AddMembersActivity::class.java)
            startActivity(intent)
        }

        cancelButton.setOnClickListener {
            finish()
        }

        saveGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString()
            if (groupName.isNotEmpty()) {
                Toast.makeText(this, "Grupo guardado", Toast.LENGTH_SHORT).show()
                // Guardar el grupo en una base de datos o lista
                finish()
            } else {
                Toast.makeText(this, "Debes asignar un nombre al grupo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
