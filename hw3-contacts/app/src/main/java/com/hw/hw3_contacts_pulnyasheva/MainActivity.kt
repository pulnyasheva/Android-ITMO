package com.hw.hw3_contacts_pulnyasheva

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


data class User(val name: String, val phone: String)

val usersList: MutableList<User> = mutableListOf()

class UserViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val nameView: TextView = root.findViewById(R.id.first_name)
    val phoneView: TextView = root.findViewById(R.id.last_name)

    fun bind(user: User) {
        nameView.text = user.name
        phoneView.text = user.phone
    }
}


class UserAdapter(
    private val users: List<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<UserViewHolder>() {

    val WHITE_LAYOUT = 101
    val PURPLE_LAYOUT = 102

    override fun getItemViewType(position: Int): Int = when {
        position % 2 == 1 -> PURPLE_LAYOUT
        else -> WHITE_LAYOUT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val holder = UserViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.recyclerview_item_white, parent, false)
        )
        holder.nameView.setOnClickListener {
            onClick(users[holder.absoluteAdapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val type: Int = getItemViewType(position)
        holder.bind(users[position])
        when (type) {
            PURPLE_LAYOUT -> {
                holder.nameView.setBackgroundColor(Color.parseColor("#EAB7F3"))
                holder.phoneView.setBackgroundColor(Color.parseColor("#EAB7F3"))
            }
        }
    }

    override fun getItemCount(): Int = usersList.size
}

class MainActivity : AppCompatActivity() {
    lateinit var myRecyclerView: RecyclerView
    val REQUEST_CODE_PERMISSION_READ_CONTACTS = 1

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@MainActivity, // Контекст
                arrayOf(Manifest.permission.READ_CONTACTS), // Что спрашиваем
                REQUEST_CODE_PERMISSION_READ_CONTACTS
            ) // Пользовательская константа для уникальности запроса
        } else {
            fullist()
        }

//        onRequestPermissionsResult(REQUEST_CODE_PERMISSION_READ_CONTACTS, arrayOf(Manifest.permission.READ_CONTACTS), IntArray(1, {PackageManager.PERMISSION_GRANTED}))




    }

    @SuppressLint("Range")
    fun readContact() {
        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (phones?.moveToNext() == true) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            usersList.add(User(name, phoneNumber))
            Log.d(name, phoneNumber)
        }
        phones?.close()
        myRecyclerView = findViewById(R.id.recyclerView)
        val viewManager = LinearLayoutManager(this)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = UserAdapter(usersList) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.phone}"))
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_READ_CONTACTS) {
            // grantResults пуст, если пользователь отменил диалог
            // (но не согласился или отказался)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fullist()
            } else {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Предупреждение")
                    .setMessage("Без разрешения приложение не будет работать корректно")
                    .setPositiveButton("ОК") { dialog, id ->
                        dialog.cancel()
                    }
                val alert = builder.create()
                alert.show()
            }
            return
        }
    }

    fun fullist() {
        readContact()
    }
}