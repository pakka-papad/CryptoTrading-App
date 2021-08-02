# CryptoTrading-App

##Overview

A simple app that authenticates users and let them check the current and historical prices of popular cryptocurrencies. The prices can be checked in USD, INR, EUR, etc. 

The data is fetched using the coinapi (https://www.coinapi.io). 

The backend is handled using firebase.

##Usage

Once the app is installed, the user needs to sign-in using a Google account.

Once signed-in the user lands on the home page of the app. Here, clicking on the star icon will pop out a list of cryptocurrencies. The user can choose the items of his choice and press "ADD" to add the selected items to his favourites list.

The user can also change the preferred currency by clicking on the "$" icon. By default, this is set to USD.

Clicking on any of the listed cryptocurrencies will take the user to a new page where the current day's highest price is mentioned. The highest and lowest price is shown for last 7 days. The user can click on "Choose Date Range" and select a range of dates to view the prices of selected days.

The user can sign-out by clicking on the profile picture on home page.
