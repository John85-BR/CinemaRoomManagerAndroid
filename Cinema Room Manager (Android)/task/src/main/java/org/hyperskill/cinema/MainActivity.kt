package org.hyperskill.cinema

import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.children
import org.hyperskill.cinema.databinding.ActivityMainBinding
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var availSeats = 56
    private var occupSeats = 0
    private var currentIncome = 0.0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP or Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val duration = intent.getIntExtra("DURATION", 108)
        val rating = intent.getFloatExtra("RATING", 4.5f)
        val movie_duration_profit = -(1.0/90.0)*(duration * duration) + 2.0*duration+90.0
        val ticketPrice = rating * movie_duration_profit / 56
        var totalIncome = 0.0

        for(row in 1..7){
            for(seat in 1..8){
                val min = 0.5
                val max = 1.5
                val price = when(row){
                    1 -> max * ticketPrice
                    else -> (max - (((max-min)/7)*(row-1))) * ticketPrice
                }
                totalIncome+=price
            }
        }



        binding.cinemaRoomTicketPrice.text = String.format("Estimated ticket price: %.2f$",ticketPrice)
        binding.cinemaRoomTotalIncome.text = String.format("Total cinema income: %.2f$",totalIncome)
        binding.cinemaRoomCurrentIncome.text = String.format("Current cinema income: %.2f$",currentIncome)
        binding.cinemaRoomAvailableSeats.text = String.format("Available seats: %d",availSeats)
        binding.cinemaRoomOccupiedSeats.text = String.format("Occupied seats: %d",occupSeats)

        binding.cinemaRoomPlaces.apply {
            for(row in 1..7){
                for(seat in 1..8){
                    addView(CardView(context).apply {
                        layoutParams = GridLayout.LayoutParams(
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                        )
                        layoutParams.height = 0
                        layoutParams.width = 0
                        setCardBackgroundColor(Color.GRAY)
                        val params = layoutParams as ViewGroup.MarginLayoutParams
                        params.setMargins(10,10,10,10)

                        id = R.id.cinema_room_place_indicator

                        setOnClickListener {
                            val min = 0.5
                            val max = 1.5
                            val price = when(row){
                                1 -> max * ticketPrice
                                else -> (max - (((max-min)/7)*(row-1))) * ticketPrice
                            }

                            if(isEnabled){
                                AlertDialog.Builder(context)
                                    .setTitle("Buy a ticket $row row $seat place")
                                    .setMessage(String.format("Your ticket price is %.2f$",price))
                                    .setPositiveButton("BUY A TICKET") { _, _ ->

                                        setCardBackgroundColor(Color.DKGRAY)
                                        isEnabled = false
                                        availSeats--
                                        occupSeats++
                                        currentIncome += price
                                        binding.cinemaRoomCurrentIncome.text = String.format("Current cinema income: %.2f$",currentIncome)
                                        binding.cinemaRoomAvailableSeats.text = String.format("Available seats: %d",availSeats)
                                        binding.cinemaRoomOccupiedSeats.text = String.format("Occupied seats: %d",occupSeats)

                                    }
                                    .setNegativeButton("CANCEL PURCHASE", null)
                                    .show()
                            }

                        }
                        addView(TextView(context).apply {
                            text = "$row.$seat"
                            id = R.id.cinema_room_place_item_text
                            gravity = CENTER
                        })
                    })
                }
            }
        }
    }
}