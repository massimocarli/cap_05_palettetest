package uk.co.massimocarli.palettetest

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

  companion object {
    const val PICK_IMAGE_REQUEST_ID = 1
  }

  /**
   * The Bitmap from the gallery
   */
  private var bitmap: Bitmap? = null

  /**
   * The current Palette
   */
  private var palette: Palette? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // We get the reference to the ImageView
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        showPaletteColor()
      }

      override fun onNothingSelected(parent: AdapterView<*>) {
        showPaletteColor()
      }
    }
  }

  fun selectImage(view: View) {
    val i = Intent(
      Intent.ACTION_PICK,
      android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    startActivityForResult(i, PICK_IMAGE_REQUEST_ID)
  }

  fun extractPalette(view: View) {
    if (bitmap != null) {
      // We get the current Palette
      Palette.Builder(bitmap!!)
        .generate { palette ->
          this@MainActivity.palette = palette
          showPaletteColor()
        }
    } else {
      Toast.makeText(this, R.string.select_image_error, Toast.LENGTH_SHORT).show()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == PICK_IMAGE_REQUEST_ID && resultCode == Activity.RESULT_OK) {
      // We extract the image from ContentProvider
      try {
        val inputStream = contentResolver.openInputStream(data!!.data!!)
        bitmap = BitmapFactory.decodeStream(inputStream)
        imageView.setImageBitmap(bitmap)
      } catch (e: FileNotFoundException) {
        bitmap = null
        Toast.makeText(this, R.string.pick_image_error, Toast.LENGTH_SHORT).show()
      }

    } else {
      bitmap = null
      Toast.makeText(this, R.string.pick_image_error, Toast.LENGTH_SHORT).show()
    }
  }

  private fun showPaletteColor() {
    if (bitmap == null) {
      Toast.makeText(this, R.string.select_image_error, Toast.LENGTH_SHORT).show()
      return
    }
    when (spinner.selectedItemPosition) {
      0 -> showColor(palette!!.getVibrantColor(Color.LTGRAY))  // Vibrant
      1 -> showColor(palette!!.getLightVibrantColor(Color.LTGRAY)) // Vibrant Light
      2 -> showColor(palette!!.getDarkVibrantColor(Color.LTGRAY)) // Vibrant Dark
      3 -> showColor(palette!!.getMutedColor(Color.LTGRAY)) // Mute
      4 -> showColor(palette!!.getLightMutedColor(Color.LTGRAY)) // Mute Light
      5 -> showColor(palette!!.getDarkMutedColor(Color.LTGRAY)) // Mute Dark
      else -> {
        throw IllegalArgumentException("Something wrong!")
      }
    }
  }

  private fun showColor(color: Int) {
    imageView.setBackgroundColor(color)
    container.setBackgroundColor(color)
  }
}
