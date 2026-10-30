package com.shayarishaala.shayarishaala.Model.Routing

import android.net.Uri

 sealed class ShayariRoutingItems(val route:String) {

     /** Builds "route/<arg>" with the argument URL-encoded; Navigation decodes it automatically. */
     fun withArg(arg: String): String = "$route/${Uri.encode(arg)}"

     object  splashScreen : ShayariRoutingItems("splash")
     object  categoryScreen : ShayariRoutingItems("category")
     object  quoteScreen : ShayariRoutingItems("quote")
     object  shayriListScreen : ShayariRoutingItems("shayari")
     object  finalShayriScreen : ShayariRoutingItems("final_shayari") {
         const val TYPE_SHAYARI = "shayari"
         const val TYPE_QUOTE = "quote"

         /** Full route pattern: optional args let the reader page through the item's category. */
         val pattern = "$route/{item}?type={type}&title={title}&index={index}"

         /** Opens [item] in the reader, swipeable through the [type]/[title] category from [index]. */
         fun reader(item: String, type: String, title: String, index: Int): String =
             "$route/${Uri.encode(item)}?type=${Uri.encode(type)}&title=${Uri.encode(title)}&index=$index"
     }
     object shayariAndQuotes : ShayariRoutingItems("shayariAndQuote")
     object quoteListItem : ShayariRoutingItems("quote_list")
     object kalamScreen : ShayariRoutingItems("kalam_screen")
     object favoritesScreen : ShayariRoutingItems("favorites")
     object studioScreen : ShayariRoutingItems("shayari_studio")

 }
