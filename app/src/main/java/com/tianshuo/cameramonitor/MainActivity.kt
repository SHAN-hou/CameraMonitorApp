package com.tianshuo.cameramonitor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tianshuo.cameramonitor.view.*

class MainActivity : AppCompatActivity() {

    // Views
    private lateinit var leftMenu: LinearLayout
    private lateinit var subMenuPanel: LinearLayout
    private lateinit var subMenuTitle: TextView
    private lateinit var subMenuList: RecyclerView
    private lateinit var displayArea: FrameLayout
    private lateinit var imageDisplay: ImageView
    private lateinit var aspectRatioOverlay: AspectRatioOverlayView
    private lateinit var zebraOverlay: ZebraOverlayView
    private lateinit var gridOverlay: GridOverlayView
    private lateinit var centerMarker: CenterMarkerView
    private lateinit var safeAreaOverlay: SafeAreaOverlayView
    private lateinit var focusPeaking: FocusPeakingView
    private lateinit var histogramView: HistogramView
    private lateinit var tapHint: TextView

    private lateinit var btnMenuDisplay: TextView
    private lateinit var btnMenuFunction: TextView
    private lateinit var btnMenuSettings: TextView

    private lateinit var btnFrame: LinearLayout
    private lateinit var btnMono: LinearLayout
    private lateinit var btnZebra: LinearLayout
    private lateinit var btnFrameLabel: TextView
    private lateinit var btnMonoLabel: TextView
    private lateinit var btnZebraLabel: TextView

    // State
    private var currentMonoMode: MonoMode = MonoMode.OFF
    private var currentFrameRatio: AspectRatioOverlayView.AspectRatio = AspectRatioOverlayView.AspectRatio.NONE
    private var currentZebraMode: ZebraOverlayView.ZebraMode = ZebraOverlayView.ZebraMode.OFF
    private var currentGridType: GridOverlayView.GridType = GridOverlayView.GridType.NONE
    private var activeMenu: String? = null

    // Monochrome filter
    private var monoColorFilter: ColorMatrixColorFilter? = null

    enum class MonoMode(val label: String) {
        OFF("关闭"),
        RED("红色"),
        GREEN("绿色"),
        BLUE("蓝色")
    }

    // Image picker
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageDisplay.setImageURI(it)
            tapHint.visibility = View.GONE
            zebraOverlay.sourceImageView = imageDisplay
            focusPeaking.sourceImageView = imageDisplay
            histogramView.sourceImageView = imageDisplay
            refreshOverlays()
        }
    }

    // Permission request
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            pickImage.launch("image/*")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUI()
        bindViews()
        setupTestPattern()
        setupLeftMenu()
        setupBottomToolbar()
        setupDisplayAreaClick()
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        }
    }

    private fun bindViews() {
        leftMenu = findViewById(R.id.leftMenu)
        subMenuPanel = findViewById(R.id.subMenuPanel)
        subMenuTitle = findViewById(R.id.subMenuTitle)
        subMenuList = findViewById(R.id.subMenuList)
        displayArea = findViewById(R.id.displayArea)
        imageDisplay = findViewById(R.id.imageDisplay)
        aspectRatioOverlay = findViewById(R.id.aspectRatioOverlay)
        zebraOverlay = findViewById(R.id.zebraOverlay)
        gridOverlay = findViewById(R.id.gridOverlay)
        centerMarker = findViewById(R.id.centerMarker)
        safeAreaOverlay = findViewById(R.id.safeAreaOverlay)
        focusPeaking = findViewById(R.id.focusPeaking)
        histogramView = findViewById(R.id.histogramView)
        tapHint = findViewById(R.id.tapHint)

        btnMenuDisplay = findViewById(R.id.btnMenuDisplay)
        btnMenuFunction = findViewById(R.id.btnMenuFunction)
        btnMenuSettings = findViewById(R.id.btnMenuSettings)

        btnFrame = findViewById(R.id.btnFrame)
        btnMono = findViewById(R.id.btnMono)
        btnZebra = findViewById(R.id.btnZebra)
        btnFrameLabel = findViewById(R.id.btnFrameLabel)
        btnMonoLabel = findViewById(R.id.btnMonoLabel)
        btnZebraLabel = findViewById(R.id.btnZebraLabel)

        subMenuList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupTestPattern() {
        // Generate a color bar test pattern
        val w = 960
        val h = 540
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val colors = intArrayOf(
            Color.WHITE, Color.YELLOW, Color.CYAN, Color.GREEN,
            Color.MAGENTA, Color.RED, Color.BLUE, Color.BLACK
        )
        val barWidth = w / colors.size
        val paint = Paint()

        for (i in colors.indices) {
            paint.color = colors[i]
            canvas.drawRect(
                (i * barWidth).toFloat(), 0f,
                ((i + 1) * barWidth).toFloat(), h * 0.7f,
                paint
            )
        }

        // Bottom gradient bar
        for (x in 0 until w) {
            val gray = (x * 255f / w).toInt()
            paint.color = Color.rgb(gray, gray, gray)
            canvas.drawRect(x.toFloat(), h * 0.7f, (x + 1).toFloat(), h * 0.85f, paint)
        }

        // Bottom black bar with steps
        val steps = 8
        val stepW = w / steps
        for (i in 0 until steps) {
            val gray = (i * 255f / steps).toInt()
            paint.color = Color.rgb(gray, gray, gray)
            canvas.drawRect(
                (i * stepW).toFloat(), h * 0.85f,
                ((i + 1) * stepW).toFloat(), h.toFloat(),
                paint
            )
        }

        imageDisplay.setImageBitmap(bitmap)
        tapHint.visibility = View.VISIBLE

        zebraOverlay.sourceImageView = imageDisplay
        focusPeaking.sourceImageView = imageDisplay
        histogramView.sourceImageView = imageDisplay
    }

    private fun setupLeftMenu() {
        btnMenuDisplay.setOnClickListener { toggleSubMenu("display") }
        btnMenuFunction.setOnClickListener { toggleSubMenu("function") }
        btnMenuSettings.setOnClickListener { toggleSubMenu("settings") }
    }

    private fun toggleSubMenu(menu: String) {
        if (activeMenu == menu && subMenuPanel.visibility == View.VISIBLE) {
            subMenuPanel.visibility = View.GONE
            activeMenu = null
            updateMenuSelection(null)
            return
        }

        activeMenu = menu
        updateMenuSelection(menu)

        when (menu) {
            "display" -> showDisplayMenu()
            "function" -> showFunctionMenu()
            "settings" -> showSettingsMenu()
        }

        subMenuPanel.visibility = View.VISIBLE
    }

    private fun updateMenuSelection(menu: String?) {
        btnMenuDisplay.isSelected = menu == "display"
        btnMenuFunction.isSelected = menu == "function"
        btnMenuSettings.isSelected = menu == "settings"
    }

    private fun showDisplayMenu() {
        subMenuTitle.text = "显示设置"
        val items = listOf(
            SubMenuItem("亮度", "调节画面亮度") { showSliderDialog("亮度", 0, 100, 50) },
            SubMenuItem("对比度", "调节画面对比度") { showSliderDialog("对比度", 0, 100, 50) },
            SubMenuItem("饱和度", "调节画面饱和度") { showSliderDialog("饱和度", 0, 100, 50) },
            SubMenuItem("色温", "调节画面色温") { showSliderDialog("色温", 3200, 6500, 5600) },
            SubMenuItem("音量", "调节音量") { showSliderDialog("音量", 0, 100, 50) }
        )
        subMenuList.adapter = SubMenuAdapter(items)
    }

    private fun showFunctionMenu() {
        subMenuTitle.text = "功能设置"
        val items = listOf(
            SubMenuItem("对焦辅助", if (focusPeaking.enabled) "已开启" else "已关闭") {
                focusPeaking.enabled = !focusPeaking.enabled
                showFunctionMenu()
            },
            SubMenuItem("直方图", if (histogramView.visibility == View.VISIBLE) "已开启" else "已关闭") {
                histogramView.visibility = if (histogramView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                histogramView.updateHistogram()
                showFunctionMenu()
            },
            SubMenuItem("网格线", currentGridType.label) {
                cycleGridType()
                showFunctionMenu()
            },
            SubMenuItem("中心标记", if (centerMarker.showMarker) "已开启" else "已关闭") {
                centerMarker.showMarker = !centerMarker.showMarker
                showFunctionMenu()
            },
            SubMenuItem("安全区", if (safeAreaOverlay.showSafeArea) "已开启" else "已关闭") {
                safeAreaOverlay.showSafeArea = !safeAreaOverlay.showSafeArea
                showFunctionMenu()
            }
        )
        subMenuList.adapter = SubMenuAdapter(items)
    }

    private fun showSettingsMenu() {
        subMenuTitle.text = "系统设置"
        val items = listOf(
            SubMenuItem("加载图片", "从相册选择图片") { requestImageLoad() },
            SubMenuItem("恢复测试图", "显示默认色彩测试图案") {
                setupTestPattern()
                applyMonoMode()
                refreshOverlays()
            },
            SubMenuItem("关于", "Camera Monitor v1.0.0") {
                Toast.makeText(this, "Camera Monitor v1.0.0\n专业摄影监视器模拟APP", Toast.LENGTH_LONG).show()
            },
            SubMenuItem("恢复默认", "重置所有设置") { resetAll() }
        )
        subMenuList.adapter = SubMenuAdapter(items)
    }

    private fun cycleGridType() {
        currentGridType = when (currentGridType) {
            GridOverlayView.GridType.NONE -> GridOverlayView.GridType.THIRDS
            GridOverlayView.GridType.THIRDS -> GridOverlayView.GridType.CROSSHAIR
            GridOverlayView.GridType.CROSSHAIR -> GridOverlayView.GridType.NONE
        }
        gridOverlay.gridType = currentGridType
    }

    private fun showSliderDialog(title: String, min: Int, max: Int, defaultVal: Int) {
        Toast.makeText(this, "$title: $defaultVal", Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomToolbar() {
        // Frame ratio shortcut
        btnFrame.setOnClickListener { cycleFrameRatio() }

        // Mono mode shortcut
        btnMono.setOnClickListener { cycleMonoMode() }

        // Zebra shortcut
        btnZebra.setOnClickListener { cycleZebraMode() }
    }

    private fun cycleFrameRatio() {
        val ratios = AspectRatioOverlayView.AspectRatio.entries.toTypedArray()
        val currentIndex = ratios.indexOf(currentFrameRatio)
        val nextIndex = (currentIndex + 1) % ratios.size
        currentFrameRatio = ratios[nextIndex]
        aspectRatioOverlay.currentRatio = currentFrameRatio

        val label = if (currentFrameRatio == AspectRatioOverlayView.AspectRatio.NONE) {
            "画幅框"
        } else {
            currentFrameRatio.label
        }
        btnFrameLabel.text = label
        updateToolbarActiveState(btnFrameLabel, currentFrameRatio != AspectRatioOverlayView.AspectRatio.NONE)
    }

    private fun cycleMonoMode() {
        val modes = MonoMode.entries.toTypedArray()
        val currentIndex = modes.indexOf(currentMonoMode)
        val nextIndex = (currentIndex + 1) % modes.size
        currentMonoMode = modes[nextIndex]

        applyMonoMode()

        val label = if (currentMonoMode == MonoMode.OFF) "单色" else currentMonoMode.label
        btnMonoLabel.text = label
        updateToolbarActiveState(btnMonoLabel, currentMonoMode != MonoMode.OFF)
    }

    private fun applyMonoMode() {
        when (currentMonoMode) {
            MonoMode.OFF -> {
                imageDisplay.colorFilter = null
            }
            MonoMode.RED -> {
                val cm = ColorMatrix(floatArrayOf(
                    1f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
                imageDisplay.colorFilter = ColorMatrixColorFilter(cm)
            }
            MonoMode.GREEN -> {
                val cm = ColorMatrix(floatArrayOf(
                    0f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
                imageDisplay.colorFilter = ColorMatrixColorFilter(cm)
            }
            MonoMode.BLUE -> {
                val cm = ColorMatrix(floatArrayOf(
                    0f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
                imageDisplay.colorFilter = ColorMatrixColorFilter(cm)
            }
        }
    }

    private fun cycleZebraMode() {
        val modes = ZebraOverlayView.ZebraMode.entries.toTypedArray()
        val currentIndex = modes.indexOf(currentZebraMode)
        val nextIndex = (currentIndex + 1) % modes.size
        currentZebraMode = modes[nextIndex]
        zebraOverlay.mode = currentZebraMode

        val label = if (currentZebraMode == ZebraOverlayView.ZebraMode.OFF) "斑马纹" else currentZebraMode.label
        btnZebraLabel.text = label
        updateToolbarActiveState(btnZebraLabel, currentZebraMode != ZebraOverlayView.ZebraMode.OFF)
    }

    private fun updateToolbarActiveState(label: TextView, active: Boolean) {
        label.setTextColor(
            if (active) ContextCompat.getColor(this, R.color.toolbar_active)
            else ContextCompat.getColor(this, R.color.text_secondary)
        )
    }

    private fun refreshOverlays() {
        aspectRatioOverlay.invalidate()
        zebraOverlay.invalidate()
        gridOverlay.invalidate()
        centerMarker.invalidate()
        safeAreaOverlay.invalidate()
        focusPeaking.invalidate()
        histogramView.updateHistogram()
    }

    private fun requestImageLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickImage.launch("image/*")
            } else {
                requestPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickImage.launch("image/*")
            } else {
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun resetAll() {
        currentFrameRatio = AspectRatioOverlayView.AspectRatio.NONE
        currentMonoMode = MonoMode.OFF
        currentZebraMode = ZebraOverlayView.ZebraMode.OFF
        currentGridType = GridOverlayView.GridType.NONE

        aspectRatioOverlay.currentRatio = currentFrameRatio
        imageDisplay.colorFilter = null
        zebraOverlay.mode = currentZebraMode
        gridOverlay.gridType = currentGridType
        centerMarker.showMarker = false
        safeAreaOverlay.showSafeArea = false
        focusPeaking.enabled = false
        histogramView.visibility = View.GONE

        btnFrameLabel.text = "画幅框"
        btnMonoLabel.text = "单色"
        btnZebraLabel.text = "斑马纹"

        updateToolbarActiveState(btnFrameLabel, false)
        updateToolbarActiveState(btnMonoLabel, false)
        updateToolbarActiveState(btnZebraLabel, false)

        setupTestPattern()
        Toast.makeText(this, "已恢复默认设置", Toast.LENGTH_SHORT).show()
    }

    private fun setupDisplayAreaClick() {
        imageDisplay.setOnClickListener {
            requestImageLoad()
        }
        tapHint.setOnClickListener {
            requestImageLoad()
        }
    }

    // Sub menu item data class
    data class SubMenuItem(
        val title: String,
        val subtitle: String,
        val onClick: () -> Unit
    )

    // Sub menu adapter
    inner class SubMenuAdapter(private val items: List<SubMenuItem>) :
        RecyclerView.Adapter<SubMenuAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(android.R.id.text1)
            val subtitle: TextView = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)
            view.setBackgroundResource(R.drawable.popup_item_bg)
            view.setPadding(16, 12, 16, 12)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.title.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text_primary))
            holder.title.textSize = 14f
            holder.subtitle.text = item.subtitle
            holder.subtitle.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text_secondary))
            holder.subtitle.textSize = 11f
            holder.itemView.setOnClickListener { item.onClick() }
        }

        override fun getItemCount() = items.size
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }
}
