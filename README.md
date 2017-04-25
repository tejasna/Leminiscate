# Leminiscate
A Wallet app built as a prototype which explores design patterns and frameworks in Android.Uses DI with Dagger 2 which simplifies code for a clearer path to write testable code. It uses RxJava for functionally styed reactive programming which pushes values and functions down the system while you can react to it by transforming and combining them. It uses an MVP pattern to sperate business logic which can be tested using the JVM.

The wallet supports 5 currencies which uses a static class file with currency conversion rate dated Apr 23 2017. 

     /**
       * A temporary static currency rate class
       * Holds the currency rates for a sample space of 5 countries
       * The currency conversion should ideally take place in a secure server @link{fixer.io}
       * Using this for temporary testing
       */
      private static class ConversionTable {
        static final double USD = 1.28;
        static final double JPY = 141.21;
        static final double CHF = 1.27;
        static final double CAD = 1.72;
        static final double GBP = 1;
      }
  
  A lot of inspiration comes from : A special thanks to google.
  https://github.com/googlesamples/android-architecture
