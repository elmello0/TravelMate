package com.example.travelmate

import android.content.Intent
import android.net.Uri // Asegúrate de incluir esta línea
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(
    private val documentList: List<Document>,
    private val onDownloadClick: (Document) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val documentName: TextView = itemView.findViewById(R.id.tvDocumentName)
        val uploadedBy: TextView = itemView.findViewById(R.id.tvUploadedBy) // Mostrar quién subió el documento
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
        holder.uploadedBy.text = "Subido por: ${currentDocument.uploadedBy}"

        holder.downloadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(currentDocument.filePath), "*/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = documentList.size
}
