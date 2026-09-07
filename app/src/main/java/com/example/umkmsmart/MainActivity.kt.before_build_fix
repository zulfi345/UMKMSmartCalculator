package com.example.umkmsmart

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {
    private val green = Color.rgb(7,117,70)
    private val darkGreen = Color.rgb(4,79,48)
    private val softGreen = Color.rgb(229,246,237)
    private val bgLight = Color.rgb(247,249,248)
    private val textDark = Color.rgb(28,47,42)
    private val muted = Color.rgb(105,120,115)
    private val red = Color.rgb(205,70,60)

    private lateinit var prefs: SharedPreferences
    private var darkMode = false
    private var currentPage = "dashboard"
    private val history = ArrayDeque<String>()
    private val cart = linkedMapOf<String, Int>()
    private var selectedCategory = "Semua"
    private var searchQuery = ""
    private var pendingFileAction = ""
    private var cashierListView: LinearLayout? = null
    private var cashierBottomView: LinearLayout? = null

    data class Product(var id:String, var name:String, var sellPrice:Int, var buyPrice:Int, var stock:Int, var minStock:Int, var category:String, var description:String="")
    data class Tx(var id:String, var title:String, var amount:Int, var type:String, var date:String, var productId:String="", var qty:Int=0)
    data class Business(var name:String="VECTA HUB UMKM", var owner:String="", var phone:String="", var address:String="", var category:String="UMKM", var description:String="")

    private val products = mutableListOf<Product>()
    private val transactions = mutableListOf<Tx>()
    private var business = Business()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("vecta_umkm_data", MODE_PRIVATE)
        loadAll()
        setupNotificationChannel()
        showSplash()
    }

    override fun onBackPressed() {
        if (history.isNotEmpty()) navigate(history.removeLast(), false) else if (currentPage != "dashboard") navigate("dashboard", false) else super.onBackPressed()
    }

    private fun showSplash() {
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(dp(32),dp(32),dp(32),dp(32)); background=GradientDrawable(GradientDrawable.Orientation.TL_BR,intArrayOf(Color.WHITE,softGreen,Color.rgb(205,239,220))) }
        val logo = ImageView(this).apply { setImageResource(R.drawable.vecta_umkm); scaleType=ImageView.ScaleType.CENTER_INSIDE }
        root.addView(logo, lp(dp(180),dp(180))); root.addView(text("VECTA HUB",30f,textDark,true,Gravity.CENTER),lp(-1,-2)); root.addView(text("UMKM",24f,textDark,false,Gravity.CENTER)); root.addView(text("VECTA PROJECT",14f,darkGreen,true,Gravity.CENTER),margins(-1,-2,0,dp(20),0,0))
        setContentView(root)
        Handler(Looper.getMainLooper()).postDelayed({ showLogin() }, 900)
    }

    private fun showLogin() {
        val root = screenRoot(true)
        val scroll = ScrollView(this); val box = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER_HORIZONTAL; setPadding(dp(24),dp(28),dp(24),dp(28)) }
        val logo=ImageView(this).apply { setImageResource(R.drawable.vecta_umkm); scaleType=ImageView.ScaleType.CENTER_INSIDE }
        val card=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(22),dp(22),dp(22),dp(22)); background=rounded(cardColor(),28) }
        val username=input("Email / Username"); val password=input("Password").apply { inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD }
        val login=button("Masuk").apply { setOnClickListener { history.clear(); navigate("dashboard",false) } }
        card.addView(text("Selamat Datang",25f,textColor(),true)); card.addView(text("Kelola usaha lebih mudah dalam satu aplikasi",14f,muted),margins(-1,-2,0,dp(4),0,dp(18))); card.addView(username,lp(-1,dp(58))); card.addView(password,margins(-1,dp(58),0,dp(10),0,dp(14))); card.addView(login,lp(-1,dp(54)))
        box.addView(logo,lp(dp(150),dp(150))); box.addView(card,margins(-1,-2,0,dp(10),0,0)); scroll.addView(box); root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f)); setContentView(root); currentPage="login"
    }

    private fun navigate(page:String, addHistory:Boolean=true) {
        if (addHistory && currentPage != page && currentPage != "login") history.addLast(currentPage)
        currentPage=page
        when(page) {
            "dashboard"->showDashboard(); "cashier"->showCashier(); "products"->showProducts(); "calculator"->showCalculator(); "expense"->showExpense(); "sale"->showSaleForm(); "report"->showReport(); "transactions"->showTransactions(); "settings"->showSettings(); "business"->showBusiness(); "backup"->showBackup(); "notifications"->showNotifications(); else->showDashboard()
        }
    }
    private fun back() { onBackPressed() }

    private fun showDashboard() {
        val root=screenRoot(); val scroll=ScrollView(this); val content=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(16),dp(16),dp(16),dp(16)) }
        val header=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(20),dp(18),dp(20),dp(18)); background=rounded(darkGreen,24) }
        header.addView(text(business.name.ifBlank { "VECTA HUB UMKM" },18f,Color.WHITE,true)); header.addView(text("Halo! Kelola usaha lebih mudah 👋",14f,Color.WHITE),margins(-1,-2,0,dp(6),0,0)); content.addView(header)
        val todaySales=transactions.filter { it.type=="Penjualan" && it.date==today() }.sumOf { it.amount }; val todayExpense=transactions.filter { it.type=="Pengeluaran" && it.date==today() }.sumOf { it.amount }
        content.addView(statCard("Omzet Hari Ini",rupiah(todaySales),"Transaksi hari ini"),margins(-1,-2,0,dp(14),0,0))
        val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }; row.addView(smallStat("Keuntungan",rupiah(todaySales-todayExpense)),weightMargins(1f,0,0,dp(6),0)); row.addView(smallStat("Produk","${products.size} Produk"),weightMargins(1f,dp(6),0,0,0)); content.addView(row)
        content.addView(sectionTitle("Menu Utama"),margins(-1,-2,0,dp(20),0,dp(10)))
        addMenuGrid(content,"🛒","Kasir",{navigate("cashier")},"🧮","Kalkulator",{navigate("calculator")})
        addMenuGrid(content,"📦","Manajemen Stok",{navigate("products")},"💸","Catat Pengeluaran",{navigate("expense")})
        addMenuGrid(content,"🧾","Catat Penjualan",{navigate("sale")},"📊","Laporan Usaha",{navigate("report")})
        addMenuGrid(content,"🧾","Transaksi",{navigate("transactions")},"⚙","Pengaturan",{navigate("settings")})
        content.addView(sectionTitle("Transaksi Terbaru"),margins(-1,-2,0,dp(18),0,dp(8)))
        transactions.takeLast(5).asReversed().forEach { content.addView(transactionCard(it),margins(-1,-2,0,0,0,dp(8))) }
        scroll.addView(content); root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f)); root.addView(bottomNav("Beranda")); setContentView(root)
    }

    private fun showCashier() {
        val root=screenRoot(); root.addView(topBar("Kasir") { back() })
        val search=input("Cari produk...").apply { setSingleLine(true); setText(searchQuery); addTextChangedListener(SimpleTextWatcher { searchQuery=it; renderCashierList() }) }
        root.addView(search,margins(-1,dp(58),dp(16),dp(10),dp(16),dp(8)))
        val category=HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled=false }; val cats=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; setPadding(dp(16),0,dp(16),dp(8)) }
        listOf("Semua","Makanan","Minuman").forEach { name -> val b=button(name).apply { textSize=12f; background=rounded(if(name==selectedCategory) green else inputColor(),14); setTextColor(if(name==selectedCategory) Color.WHITE else textColor()); setOnClickListener { selectedCategory=name; showCashier() } }; cats.addView(b,margins(dp(110),dp(48),0,0,dp(6),0)) }; category.addView(cats); root.addView(category)
        val scroll=ScrollView(this); val list=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(16),dp(4),dp(16),dp(12)) }; cashierListView = list; scroll.addView(list); root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f))
        val bottom=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL; setPadding(dp(16),dp(10),dp(16),dp(12)); background=rounded(cardColor(),0) }; cashierBottomView = bottom; root.addView(bottom); setContentView(root); renderCashierList(); renderCashierBottom(bottom)
    }
    private fun renderCashierList() {
        val list=cashierListView ?: return; list.removeAllViews(); products.filter { (selectedCategory=="Semua" || it.category==selectedCategory) && (searchQuery.isBlank() || it.name.contains(searchQuery,true)) }.forEach { list.addView(cashierProduct(it),margins(-1,-2,0,0,0,dp(10))) }
    }
    private fun renderCashierBottom(bottom:LinearLayout?=cashierBottomView) {
        bottom ?: return; bottom.removeAllViews(); val count=cart.values.sum(); val total=cart.sumOf { (id,q)-> products.find{it.id==id}?.sellPrice?.times(q) ?: 0 }; bottom.addView(text("$count Item\n${rupiah(total)}",14f,textColor(),true),LinearLayout.LayoutParams(0,dp(58),1f)); val pay=button("Bayar").apply { setOnClickListener { checkout() } }; bottom.addView(pay,lp(dp(120),dp(54)))
    }
    private fun cashierProduct(p:Product):View {
        val card=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL; setPadding(dp(16),dp(14),dp(12),dp(14)); background=rounded(cardColor(),18) }
        val info=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }; info.addView(text(p.name,16f,textColor(),true)); info.addView(text("${rupiah(p.sellPrice)} • Stok ${p.stock}",13f,muted)); val qty=cart[p.id]?:0
        val controls=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        if(qty>0) { controls.addView(smallButton("−") { changeCart(p,-1) }); controls.addView(text(qty.toString(),16f,textColor(),true,Gravity.CENTER),lp(dp(38),dp(48))) }
        controls.addView(smallButton("+") { changeCart(p,1) })
        card.addView(info,LinearLayout.LayoutParams(0,-2,1f)); card.addView(controls); return card
    }
    private fun changeCart(p:Product, delta:Int) { val old=cart[p.id]?:0; val next=(old+delta).coerceAtLeast(0); if(delta>0 && next>p.stock) { toast("Stok ${p.name} tidak mencukupi"); return }; if(next==0) cart.remove(p.id) else cart[p.id]=next; showCashier() }
    private fun checkout() { if(cart.isEmpty()) { toast("Keranjang masih kosong"); return }; val total=cart.sumOf { (id,q)-> products.find{it.id==id}?.sellPrice?.times(q) ?: 0 }; val methods=arrayOf("Tunai","Transfer","QRIS"); AlertDialog.Builder(this).setTitle("Pembayaran ${rupiah(total)}").setItems(methods){_,which-> confirmCheckout(methods[which],total)}.setNegativeButton("Batal",null).show() }
    private fun confirmCheckout(method:String,total:Int) { val lines=cart.toMap(); lines.forEach { (id,q)-> val p=products.find{it.id==id}?:return@forEach; p.stock-=q; transactions.add(Tx(uuid(),"${p.name} • $q pcs",p.sellPrice*q,"Penjualan",today(),id,q)) }; cart.clear(); saveAll(); notifyLowStock(); toast("Pembayaran $method berhasil: ${rupiah(total)}"); navigate("dashboard",false) }

    private fun showProducts() {
        val root=screenRoot(); root.addView(topBar("Manajemen Produk") { back() }); val add=button("+ Tambah Produk").apply { setOnClickListener { showProductDialog(null) } }; root.addView(add,margins(-1,dp(54),dp(16),dp(10),dp(16),dp(8)))
        val scroll=ScrollView(this); val list=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(16),0,dp(16),dp(16)) }; products.forEach { p-> list.addView(productCard(p),margins(-1,-2,0,0,0,dp(10))) }; scroll.addView(list); root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f)); root.addView(bottomNav("Produk")); setContentView(root)
    }
    private fun productCard(p:Product):View { val card=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(16),dp(14),dp(16),dp(14)); background=rounded(cardColor(),18) }; val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}; val info=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}; info.addView(text(p.name,17f,textColor(),true)); info.addView(text("${p.category} • ${rupiah(p.sellPrice)}",13f,muted)); val status=if(p.stock<=0) "Habis" else if(p.stock<=p.minStock) "Stok menipis" else "Stok aman"; info.addView(text("Stok ${p.stock} • $status",13f,if(p.stock<=p.minStock) red else green)); val edit=button("Edit").apply{setOnClickListener{showProductDialog(p)}}; val del=button("Hapus").apply{background=rounded(red,14);setOnClickListener{AlertDialog.Builder(this@MainActivity).setTitle("Hapus produk?").setMessage(p.name+" akan dihapus dari daftar.").setPositiveButton("Hapus"){_,_->products.remove(p);cart.remove(p.id);saveAll();showProducts()}.setNegativeButton("Batal",null).show()}}; row.addView(info,LinearLayout.LayoutParams(0,-2,1f)); row.addView(edit,lp(dp(76),dp(48))); row.addView(del,margins(dp(76),dp(48),dp(6),0,0,0)); card.addView(row); val controls=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}; controls.addView(button("− Stok").apply{setOnClickListener{adjustStock(p,-1)}},weightMargins(1f,0,dp(10),dp(4),0)); controls.addView(button("+ Stok").apply{setOnClickListener{adjustStock(p,1)}},weightMargins(1f,dp(4),dp(10),0,0)); card.addView(controls); return card }
    private fun adjustStock(p:Product, sign:Int) { val e=input("Jumlah",true); AlertDialog.Builder(this).setTitle(if(sign>0)"Tambah Stok" else "Kurangi Stok").setView(e).setPositiveButton("Simpan"){_,_-> val q=e.text.toString().toIntOrNull()?:0; val next=p.stock+sign*q; if(q<=0 || next<0) toast("Jumlah tidak valid") else { p.stock=next; saveAll(); showProducts() } }.setNegativeButton("Batal",null).show() }
    private fun showProductDialog(existing:Product?) { val wrap=ScrollView(this); val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL; setPadding(dp(20),dp(8),dp(20),dp(8))}; val name=input("Nama produk"); val sell=input("Harga jual",true); val buy=input("Harga modal",true); val stock=input("Stok",true); val min=input("Stok minimum",true); val category=input("Kategori: Makanan / Minuman"); val desc=input("Deskripsi"); if(existing!=null){name.setText(existing.name);sell.setText(existing.sellPrice.toString());buy.setText(existing.buyPrice.toString());stock.setText(existing.stock.toString());min.setText(existing.minStock.toString());category.setText(existing.category);desc.setText(existing.description)}; listOf(name,sell,buy,stock,min,category,desc).forEach{box.addView(it,margins(-1,dp(58),0,dp(6),0,0))}; wrap.addView(box); val dialog=AlertDialog.Builder(this).setTitle(if(existing==null)"Tambah Produk" else "Edit Produk").setView(wrap).setPositiveButton("Simpan",null).setNegativeButton("Batal",null).create(); dialog.setOnShowListener{dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{ val n=name.text.toString().trim(); val sp=sell.text.toString().toIntOrNull(); val bp=buy.text.toString().toIntOrNull(); val st=stock.text.toString().toIntOrNull(); val mn=min.text.toString().toIntOrNull(); if(n.isBlank()||sp==null||bp==null||st==null||mn==null){toast("Lengkapi semua data angka dan nama produk");return@setOnClickListener}; val cat=category.text.toString().trim().ifBlank{"Lainnya"}; if(existing==null) products.add(Product(uuid(),n,sp,bp,st,mn,cat,desc.text.toString().trim())) else {existing.name=n;existing.sellPrice=sp;existing.buyPrice=bp;existing.stock=st;existing.minStock=mn;existing.category=cat;existing.description=desc.text.toString().trim()}; saveAll(); dialog.dismiss(); showProducts()}}; dialog.show() }

    private fun showCalculator() { val root=screenRoot(); root.addView(topBar("Kalkulator Harga Jual") { back() }); val scroll=ScrollView(this); val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16))}; val modal=input("Harga modal",true); val biaya=input("Biaya tambahan",true); val markup=input("Markup (%)",true); val result=text("Isi data untuk menghitung harga jual",17f,textColor(),true); val calc=button("Hitung Harga Jual").apply{setOnClickListener{val m=modal.text.toString().toIntOrNull()?:0;val b=biaya.text.toString().toIntOrNull()?:0;val u=markup.text.toString().toDoubleOrNull()?:0.0;val base=m+b;val profit=(base*u/100).toInt();result.text="Modal total: ${rupiah(base)}\nMarkup: ${rupiah(profit)}\nHarga jual: ${rupiah(base+profit)}"}}; box.addView(modal,lp(-1,dp(60)));box.addView(biaya,margins(-1,dp(60),0,dp(10),0,0));box.addView(markup,margins(-1,dp(60),0,dp(10),0,dp(16)));box.addView(calc,lp(-1,dp(54)));box.addView(result,margins(-1,-2,0,dp(20),0,0));scroll.addView(box);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));setContentView(root) }

    private fun showExpense() { val root=screenRoot(); root.addView(topBar("Catat Pengeluaran") { back() }); val box=formContainer(); val category=input("Kategori pengeluaran"); val amount=input("Jumlah",true); val note=input("Keterangan"); val save=button("Simpan Pengeluaran").apply{setOnClickListener{val a=amount.text.toString().toIntOrNull()?:0;if(a<=0){toast("Jumlah pengeluaran tidak valid");return@setOnClickListener};transactions.add(Tx(uuid(),note.text.toString().ifBlank{category.text.toString().ifBlank{"Pengeluaran"}},a,"Pengeluaran",today()));saveAll();toast("Pengeluaran tersimpan");navigate("transactions",false)}}; listOf(category,amount,note).forEach{box.addView(it,margins(-1,dp(60),0,0,0,dp(10)))};box.addView(save,lp(-1,dp(54)));root.addView(box,LinearLayout.LayoutParams(-1,0,1f));setContentView(root) }

    private fun showSaleForm() { val root=screenRoot(); root.addView(topBar("Catat Penjualan") { back() }); val box=formContainer(); val productName=input("Nama produk"); val qty=input("Jumlah",true); val price=input("Harga satuan",true); val save=button("Simpan Penjualan").apply{setOnClickListener{val p=products.firstOrNull{it.name.equals(productName.text.toString().trim(),true)};val q=qty.text.toString().toIntOrNull()?:0;val pr=price.text.toString().toIntOrNull()?:p?.sellPrice?:0;if(p==null){toast("Produk tidak ditemukan");return@setOnClickListener};if(q<=0||q>p.stock){toast("Jumlah melebihi stok");return@setOnClickListener};p.stock-=q;transactions.add(Tx(uuid(),"${p.name} • $q pcs",pr*q,"Penjualan",today(),p.id,q));saveAll();notifyLowStock();toast("Penjualan tersimpan");navigate("transactions",false)}}; productName.setOnFocusChangeListener{_,focus->if(!focus){products.firstOrNull{it.name.equals(productName.text.toString().trim(),true)}?.let{price.setText(it.sellPrice.toString())}}}; listOf(productName,qty,price).forEach{box.addView(it,margins(-1,dp(60),0,0,0,dp(10)))}; box.addView(text("Gunakan nama produk yang sama dengan daftar produk.",12f,muted));box.addView(save,margins(-1,dp(54),0,dp(16),0,0));root.addView(box,LinearLayout.LayoutParams(-1,0,1f));setContentView(root) }

    private fun showReport() { val root=screenRoot(); root.addView(topBar("Laporan Usaha") { back() }); val scroll=ScrollView(this); val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16))}; val sales=transactions.filter{it.type=="Penjualan"}.sumOf{it.amount}; val expenses=transactions.filter{it.type=="Pengeluaran"}.sumOf{it.amount}; val modal=transactions.filter{it.type=="Penjualan"}.sumOf{tx-> products.firstOrNull{it.id==tx.productId}?.buyPrice?.times(tx.qty) ?: 0}; val profit=sales-expenses-modal; box.addView(reportCard("Total Penjualan",rupiah(sales),green));box.addView(reportCard("Modal Produk Terjual",rupiah(modal),muted),margins(-1,-2,0,dp(10),0,0));box.addView(reportCard("Total Pengeluaran",rupiah(expenses),red),margins(-1,-2,0,dp(10),0,0));box.addView(reportCard("Keuntungan Bersih",rupiah(profit),if(profit>=0)green else red),margins(-1,-2,0,dp(10),0,0));box.addView(sectionTitle("Produk Stok Menipis"),margins(-1,-2,0,dp(22),0,dp(8)));products.filter{it.stock<=it.minStock}.ifEmpty{listOf()}.forEach{box.addView(text("• ${it.name}: stok ${it.stock}",15f,textColor()),margins(-1,-2,0,0,0,dp(6)))}; if(products.none{it.stock<=it.minStock})box.addView(text("Semua stok berada di atas batas minimum.",14f,muted));scroll.addView(box);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));root.addView(bottomNav("Laporan"));setContentView(root) }

    private fun showTransactions() { val root=screenRoot();root.addView(topBar("Riwayat Transaksi"){back()});val scroll=ScrollView(this);val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16))};transactions.asReversed().forEach{box.addView(transactionCard(it),margins(-1,-2,0,0,0,dp(8)))};scroll.addView(box);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));root.addView(bottomNav("Lainnya"));setContentView(root) }

    private fun showSettings() { val root=screenRoot();root.addView(topBar("Pengaturan"){back()});val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16))}; val items=listOf(Triple("Data Usaha","Nama usaha dan informasi toko",{navigate("business")}),Triple("Backup & Restore","Simpan dan pulihkan seluruh data",{navigate("backup")}),Triple("Notifikasi","Stok rendah dan pengingat",{navigate("notifications")}));items.forEach{box.addView(settingCard(it.first,it.second,it.third),margins(-1,-2,0,0,0,dp(8)))};box.addView(settingCard("Tema",if(darkMode)"Dark Mode aktif" else "Light Mode aktif"){darkMode=!darkMode;prefs.edit().putBoolean("dark",darkMode).apply();showSettings()},margins(-1,-2,0,0,0,dp(8)));box.addView(settingCard("Tentang Aplikasi","VECTA HUB UMKM • VECTA PROJECT"){AlertDialog.Builder(this).setTitle("VECTA HUB UMKM").setMessage("Aplikasi pengelolaan usaha UMKM. Versi 2.1").setPositiveButton("OK",null).show()});root.addView(box);root.addView(Space(this),LinearLayout.LayoutParams(-1,0,1f));root.addView(bottomNav("Lainnya"));setContentView(root) }

    private fun showBusiness() { val root=screenRoot();root.addView(topBar("Data Usaha"){back()});val box=formContainer();val name=input("Nama usaha");val owner=input("Nama pemilik");val phone=input("Nomor WhatsApp",true);val address=input("Alamat usaha");val category=input("Kategori usaha");val desc=input("Deskripsi usaha");name.setText(business.name);owner.setText(business.owner);phone.setText(business.phone);address.setText(business.address);category.setText(business.category);desc.setText(business.description);val save=button("Simpan Data Usaha").apply{setOnClickListener{business=Business(name.text.toString().trim().ifBlank{"VECTA HUB UMKM"},owner.text.toString().trim(),phone.text.toString().trim(),address.text.toString().trim(),category.text.toString().trim(),desc.text.toString().trim());saveAll();toast("Data usaha berhasil disimpan");back()}};listOf(name,owner,phone,address,category,desc).forEach{box.addView(it,margins(-1,dp(60),0,0,0,dp(10)))};box.addView(save,margins(-1,dp(54),0,dp(8),0,0));root.addView(box,LinearLayout.LayoutParams(-1,0,1f));setContentView(root) }

    private fun showNotifications() { val root=screenRoot();root.addView(topBar("Notifikasi"){back()});val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16))};val enabled=Switch(this).apply{text="Aktifkan notifikasi stok menipis";isChecked=prefs.getBoolean("notif",true);setTextColor(textColor());setOnCheckedChangeListener{_,v->prefs.edit().putBoolean("notif",v).apply()}};val limit=input("Batas stok rendah",true).apply{setText(prefs.getInt("low_limit",5).toString())};val save=button("Simpan Pengaturan").apply{setOnClickListener{prefs.edit().putInt("low_limit",limit.text.toString().toIntOrNull()?:5).apply();requestNotificationPermission();toast("Pengaturan notifikasi disimpan");notifyLowStock()}};box.addView(enabled);box.addView(limit,margins(-1,dp(60),0,dp(16),0,dp(10)));box.addView(save,lp(-1,dp(54)));root.addView(box);setContentView(root) }

    private fun showBackup() { val root=screenRoot();root.addView(topBar("Backup & Restore"){back()});val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16))};val backup=button("Buat & Simpan Backup").apply{setOnClickListener{pendingFileAction="backup";val i=Intent(Intent.ACTION_CREATE_DOCUMENT).apply{type="application/json";putExtra(Intent.EXTRA_TITLE,"VECTA-HUB-UMKM-BACKUP-${today()}.json")};startActivityForResult(i,1001)}};val restore=button("Restore dari File Backup").apply{setOnClickListener{pendingFileAction="restore";val i=Intent(Intent.ACTION_OPEN_DOCUMENT).apply{addCategory(Intent.CATEGORY_OPENABLE);type="application/json"};startActivityForResult(i,1002)}};val reset=button("Reset Data Contoh").apply{setOnClickListener{AlertDialog.Builder(this).setTitle("Reset data?").setMessage("Produk dan transaksi saat ini akan diganti dengan data contoh.").setPositiveButton("Reset"){_,_->seedDefaults(true);saveAll();toast("Data direset")}.setNegativeButton("Batal",null).show()}};box.addView(text("Backup menyimpan data usaha, produk, transaksi dan pengaturan.",14f,muted));box.addView(backup,margins(-1,dp(54),0,dp(20),0,dp(10)));box.addView(restore,lp(-1,dp(54)));box.addView(reset,margins(-1,dp(54),0,dp(10),0,0));root.addView(box);setContentView(root) }

    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) { super.onActivityResult(requestCode,resultCode,data); if(resultCode!=RESULT_OK) return; val uri=data?.data?:return; try { if(requestCode==1001){contentResolver.openOutputStream(uri)?.bufferedWriter()?.use{it.write(exportJson().toString(2))};toast("Backup berhasil disimpan")} else if(requestCode==1002){val raw=contentResolver.openInputStream(uri)?.bufferedReader()?.use{it.readText()}?:return;AlertDialog.Builder(this).setTitle("Restore backup?").setMessage("Data saat ini akan ditimpa oleh isi backup.").setPositiveButton("Restore"){_,_->importJson(JSONObject(raw));saveAll();toast("Restore berhasil");navigate("dashboard",false)}.setNegativeButton("Batal",null).show()} } catch(e:Exception){toast("Gagal memproses file backup: ${e.message}")} }

    private fun saveAll() { prefs.edit().putBoolean("dark",darkMode).putString("business",businessJson().toString()).putString("products",productsJson().toString()).putString("transactions",transactionsJson().toString()).apply() }
    private fun loadAll() { darkMode=prefs.getBoolean("dark",false); try{val b=prefs.getString("business",null);if(b!=null)businessFrom(JSONObject(b));val p=prefs.getString("products",null);if(p!=null)productsFrom(JSONArray(p));val t=prefs.getString("transactions",null);if(t!=null)transactionsFrom(JSONArray(t))}catch(_:Exception){}; if(products.isEmpty()) seedDefaults(false) }
    private fun seedDefaults(force:Boolean){if(force){products.clear();transactions.clear()};if(products.isNotEmpty()&&!force)return;products.addAll(listOf(Product(uuid(),"Keripik Singkong",5000,3000,20,5,"Makanan","Keripik singkong renyah dan gurih"),Product(uuid(),"Pisang Crispy",8000,4500,15,5,"Makanan","Pisang crispy"),Product(uuid(),"Brownies",12000,7000,8,3,"Makanan","Brownies cokelat"),Product(uuid(),"Es Teh Manis",3000,1200,30,5,"Minuman","Teh manis segar"),Product(uuid(),"Kopi Susu",7000,3500,5,3,"Minuman","Kopi susu creamy")));if(force||transactions.isEmpty()){transactions.clear();transactions.add(Tx(uuid(),"Keripik Singkong • 5 pcs",25000,"Penjualan",today()));transactions.add(Tx(uuid(),"Kopi Susu • 3 pcs",21000,"Penjualan",today()))}}
    private fun exportJson():JSONObject=JSONObject().put("version",1).put("business",businessJson()).put("products",productsJson()).put("transactions",transactionsJson()).put("dark",darkMode).put("notif",prefs.getBoolean("notif",true)).put("low_limit",prefs.getInt("low_limit",5))
    private fun importJson(o:JSONObject){businessFrom(o.optJSONObject("business")?:JSONObject());productsFrom(o.optJSONArray("products")?:JSONArray());transactionsFrom(o.optJSONArray("transactions")?:JSONArray());darkMode=o.optBoolean("dark",false);prefs.edit().putBoolean("notif",o.optBoolean("notif",true)).putInt("low_limit",o.optInt("low_limit",5)).apply()}
    private fun businessJson()=JSONObject().put("name",business.name).put("owner",business.owner).put("phone",business.phone).put("address",business.address).put("category",business.category).put("description",business.description)
    private fun businessFrom(o:JSONObject){business=Business(o.optString("name","VECTA HUB UMKM"),o.optString("owner"),o.optString("phone"),o.optString("address"),o.optString("category","UMKM"),o.optString("description"))}
    private fun productsJson()=JSONArray().apply{products.forEach{put(JSONObject().put("id",it.id).put("name",it.name).put("sell",it.sellPrice).put("buy",it.buyPrice).put("stock",it.stock).put("min",it.minStock).put("category",it.category).put("description",it.description))}}
    private fun productsFrom(a:JSONArray){products.clear();for(i in 0 until a.length()){val o=a.getJSONObject(i);products.add(Product(o.optString("id",uuid()),o.optString("name"),o.optInt("sell"),o.optInt("buy"),o.optInt("stock"),o.optInt("min",0),o.optString("category"),o.optString("description")))}}
    private fun transactionsJson()=JSONArray().apply{transactions.forEach{put(JSONObject().put("id",it.id).put("title",it.title).put("amount",it.amount).put("type",it.type).put("date",it.date).put("productId",it.productId).put("qty",it.qty))}}
    private fun transactionsFrom(a:JSONArray){transactions.clear();for(i in 0 until a.length()){val o=a.getJSONObject(i);transactions.add(Tx(o.optString("id",uuid()),o.optString("title"),o.optInt("amount"),o.optString("type"),o.optString("date"),o.optString("productId"),o.optInt("qty")))}}

    private fun requestNotificationPermission(){if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),2001)}
    private fun setupNotificationChannel(){if(Build.VERSION.SDK_INT>=26){val c=NotificationChannel("stock_alert","Peringatan Stok",NotificationManager.IMPORTANCE_DEFAULT);getSystemService(NotificationManager::class.java).createNotificationChannel(c)}}
    private fun notifyLowStock(){if(!prefs.getBoolean("notif",true))return;requestNotificationPermission();if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return;val low=products.filter{it.stock<=minOf(it.minStock,prefs.getInt("low_limit",5))};if(low.isNotEmpty()){val text=low.joinToString(", "){"${it.name} (${it.stock})"};val b=if(Build.VERSION.SDK_INT>=26)Notification.Builder(this,"stock_alert") else Notification.Builder(this);b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Stok menipis").setContentText(text).setAutoCancel(true);getSystemService(NotificationManager::class.java).notify(1001,b.build())}}

    private fun screenRoot(center:Boolean=false)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;if(center)gravity=Gravity.CENTER;setBackgroundColor(backgroundColor())}
    private fun formContainer():ScrollView { val s=ScrollView(this); val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(24))};s.addView(box);return s }
    private fun topBar(title:String,backAction:()->Unit):View{val bar=LinearLayout(this).apply{gravity=Gravity.CENTER_VERTICAL;setPadding(dp(8),dp(6),dp(16),dp(6));setBackgroundColor(darkGreen)};val b=Button(this).apply{text="←";textSize=24f;setTextColor(Color.WHITE);setBackgroundColor(Color.TRANSPARENT);setOnClickListener{backAction()}};bar.addView(b,lp(dp(56),dp(54)));bar.addView(text(title,19f,Color.WHITE,true),LinearLayout.LayoutParams(0,dp(54),1f));return bar}
    private fun bottomNav(active:String):View{val nav=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER;setPadding(dp(2),dp(4),dp(2),dp(4));setBackgroundColor(cardColor())};listOf("Beranda" to {navigate("dashboard")},"Kasir" to {navigate("cashier")},"Produk" to {navigate("products")},"Laporan" to {navigate("report")},"Lainnya" to {navigate("settings")}).forEach{(n,a)->val b=Button(this).apply{text=n;textSize=10f;setTextColor(if(n==active)green else muted);setBackgroundColor(Color.TRANSPARENT);isAllCaps=false;setOnClickListener{a()}};nav.addView(b,LinearLayout.LayoutParams(0,dp(54),1f))};return nav}
    private fun addMenuGrid(parent:LinearLayout,i1:String,t1:String,a1:()->Unit,i2:String,t2:String,a2:()->Unit){val r=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL};r.addView(menuCard(i1,t1,a1),weightMargins(1f,0,0,dp(5),0));r.addView(menuCard(i2,t2,a2),weightMargins(1f,dp(5),0,0,0));parent.addView(r,margins(-1,-2,0,0,0,dp(10)))}
    private fun menuCard(icon:String,title:String,action:()->Unit)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(dp(10),dp(16),dp(10),dp(16));background=rounded(cardColor(),18);addView(text(icon,26f,green,false,Gravity.CENTER));addView(text(title,13f,textColor(),true,Gravity.CENTER));setOnClickListener{action()}}
    private fun statCard(t:String,v:String,s:String)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(16),dp(18),dp(16));background=rounded(cardColor(),20);addView(text(t,13f,muted));addView(text(v,24f,textColor(),true));addView(text(s,12f,green))}
    private fun smallStat(t:String,v:String)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(14),dp(14),dp(14),dp(14));background=rounded(cardColor(),18);addView(text(t,12f,muted));addView(text(v,15f,textColor(),true))}
    private fun reportCard(t:String,v:String,c:Int)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(16),dp(16),dp(16));background=rounded(cardColor(),18);addView(text(t,13f,muted));addView(text(v,21f,c,true))}
    private fun transactionCard(x:Tx)=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(14),dp(13),dp(14),dp(13));background=rounded(cardColor(),16);val info=LinearLayout(this@MainActivity).apply{orientation=LinearLayout.VERTICAL;addView(text(x.type+" • "+x.date,12f,muted));addView(text(x.title,15f,textColor(),true))};addView(info,LinearLayout.LayoutParams(0,-2,1f));addView(text(rupiah(x.amount),14f,if(x.type=="Penjualan")green else red,true,Gravity.END));setOnLongClickListener{AlertDialog.Builder(this@MainActivity).setTitle("Hapus transaksi?").setMessage("Jika transaksi penjualan dihapus, stok produk akan dikembalikan.").setPositiveButton("Hapus"){_,_->if(x.type=="Penjualan"&&x.productId.isNotBlank()&&x.qty>0)products.firstOrNull{it.id==x.productId}?.let{it.stock+=x.qty};transactions.removeAll{it.id==x.id};saveAll();showTransactions()}.setNegativeButton("Batal",null).show();true}}
    private fun settingCard(t:String,s:String,a:()->Unit)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(15),dp(16),dp(15));background=rounded(cardColor(),18);addView(text(t,16f,textColor(),true));addView(text(s,12f,muted));setOnClickListener{a()}}
    private fun sectionTitle(v:String)=text(v,19f,textColor(),true)
    private fun input(h:String,numeric:Boolean=false)=EditText(this).apply{hint=h;textSize=16f;setTextColor(textColor());setHintTextColor(muted);background=rounded(inputColor(),14);setPadding(dp(16),0,dp(16),0);if(numeric)inputType=InputType.TYPE_CLASS_NUMBER}
    private fun button(label:String)=Button(this).apply{text=label;textSize=14f;setTextColor(Color.WHITE);isAllCaps=false;background=rounded(green,14);minHeight=0;minWidth=0}
    private fun smallButton(label:String,action:()->Unit)=Button(this).apply{text=label;textSize=22f;setTextColor(Color.WHITE);isAllCaps=false;background=rounded(green,14);setOnClickListener{action()};minimumWidth=0;minimumHeight=0}.also{it.layoutParams=lp(dp(52),dp(48))}
    private fun text(v:String,size:Float,color:Int,bold:Boolean=false,gravity:Int=Gravity.NO_GRAVITY)=TextView(this).apply{text=v;textSize=size;setTextColor(color);this.gravity=gravity;if(bold)setTypeface(Typeface.DEFAULT_BOLD)}
    private fun rounded(c:Int,r:Int)=GradientDrawable().apply{setColor(c);cornerRadius=dp(r).toFloat()}
    private fun backgroundColor()=if(darkMode)Color.rgb(15,25,23) else bgLight
    private fun cardColor()=if(darkMode)Color.rgb(28,42,38) else Color.WHITE
    private fun inputColor()=if(darkMode)Color.rgb(38,55,50) else Color.rgb(242,246,244)
    private fun textColor()=if(darkMode)Color.WHITE else textDark
    private fun rupiah(v:Int)=NumberFormat.getCurrencyInstance(Locale("id","ID")).format(v).replace(",00","")
    private fun toast(m:String)=Toast.makeText(this,m,Toast.LENGTH_SHORT).show()
    private fun today()=SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(Date())
    private fun uuid()=UUID.randomUUID().toString()
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
    private fun lp(w:Int,h:Int)=LinearLayout.LayoutParams(w,h)
    private fun margins(w:Int,h:Int,l:Int,t:Int,r:Int,b:Int)=LinearLayout.LayoutParams(w,h).apply{setMargins(l,t,r,b)}
    private fun weightMargins(weight:Float,l:Int,t:Int,r:Int,b:Int)=LinearLayout.LayoutParams(0,-2,weight).apply{setMargins(l,t,r,b)}
    private class SimpleTextWatcher(val changed:(String)->Unit):android.text.TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){changed(s?.toString().orEmpty())};override fun afterTextChanged(s:android.text.Editable?){}}
}
