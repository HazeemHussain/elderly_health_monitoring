package com.example.lifelineapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifelineapp.R
import com.example.lifelineapp.model.EmergencyContact

class ContactsAdapter(private var contacts: List<EmergencyContact>, private val context: Context) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.contactName.text = contact.name
        holder.contactPhone.text = contact.phoneNo
        holder.contactAddress.text = contact.address
        holder.relationship.text = contact.relationship

        // Load the image from Firebase or use a placeholder
        if (contact.imageUrl.isNotEmpty()) {
            Glide.with(context).load(contact.imageUrl).into(holder.contactImage)
        } else {
            holder.contactImage.setImageResource(R.drawable.ic_contact_placeholder)
        }

        // Call button click listener
        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${contact.phoneNo}")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = contacts.size

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.contactImage)
        val contactName: TextView = itemView.findViewById(R.id.contactName)
        val contactPhone: TextView = itemView.findViewById(R.id.contactPhone)
        val contactAddress: TextView = itemView.findViewById(R.id.contactAddress)
        val relationship: TextView = itemView.findViewById(R.id.relationship_Field)
        val callButton: Button = itemView.findViewById(R.id.callButton)
    }

    fun updateData(newContacts: List<EmergencyContact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
