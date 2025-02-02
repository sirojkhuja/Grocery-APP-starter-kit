package com.example.grocerryapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(),GroceryRVAdapter.GroceryItemClickInterface {
    lateinit var itemsRV : RecyclerView
    lateinit var addFAB : FloatingActionButton
    lateinit var list :List<GroceryItems>
    lateinit var  groceryRVAdapter: GroceryRVAdapter
    lateinit var  groceryViewModal : GroceryViewModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsRV = findViewById(R.id.idRVItems)
        addFAB = findViewById(R.id.idFABAdd)
        list =  ArrayList<GroceryItems>()
        groceryRVAdapter =  GroceryRVAdapter(list, this)
        itemsRV.layoutManager = LinearLayoutManager(this)
        itemsRV.adapter = groceryRVAdapter

        val groceryRepository = GroceryRepository(GroceryDatabase(this))
        val factory =  GroceryViewModalFactory(groceryRepository)
        groceryViewModal = ViewModelProvider(this, factory).get(GroceryViewModal::class.java)
        groceryViewModal.getAllGroceryItems().observe(this, Observer {
            groceryRVAdapter.list = it
            groceryRVAdapter.notifyDataSetChanged()
        })
        addFAB.setOnClickListener {
            openDiaLog()
        }
    }

    fun openDiaLog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.grocerry_add_dialog)

        //Button
        val cancelBtn =  dialog.findViewById<Button>(R.id.idBtnCancel)
        val addBtn =  dialog.findViewById<Button>(R.id.idBtnAdd)

        //Text
        val itemEdt =  dialog.findViewById<EditText>(R.id.idEdItemName)
        val itemPriceEdt =  dialog.findViewById<EditText>(R.id.idEdItemPrice)
        val itemQuantityEdt =  dialog.findViewById<EditText>(R.id.idEdItemQuantity)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        addBtn.setOnClickListener {
            val itemName : String = itemEdt.text.toString()
            val itemPrice : String = itemPriceEdt.text.toString()
            val itemQuantity : String = itemQuantityEdt.text.toString()

            val qty : Int = itemQuantity.toInt()
            val pr : Int =  itemPrice.toInt()

            if(itemName.isNotEmpty() && itemPrice.isNotEmpty() && itemQuantity.isNotEmpty()){
                val items = GroceryItems(itemName, qty, pr)
                groceryViewModal.insert(items)
                Toast.makeText(applicationContext, "Item inserido..", Toast.LENGTH_SHORT).show()
                groceryRVAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }else{
                Toast.makeText(applicationContext, "Por favor, entre todos os dados..", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()

    }

    override fun onItemClick(groceryItems: GroceryItems) {

        groceryViewModal.delete(groceryItems )
        groceryRVAdapter.notifyDataSetChanged()
        Toast.makeText(applicationContext, "Item excluído...", Toast.LENGTH_SHORT).show()
    }
}