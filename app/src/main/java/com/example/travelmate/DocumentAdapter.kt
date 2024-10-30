package com.example.travelmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(private val documentList: List<Document>, private val onDownloadClick: (Document) -> Unit) :
    RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val documentName: TextView = itemView.findViewById(R.id.tvDocumentName)
        val downloadButton: ImageView = itemView.findViewById(R.id.imgDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val currentDocument = documentList[position]
        holder.documentName.text = currentDocument.name

        // Acción para descargar el documento cuando se hace clic en el botón de descarga
        holder.downloadButton.setOnClickListener {
            onDownloadClick(currentDocument)
        }
    }

    override fun getItemCount() = documentList.size
}
