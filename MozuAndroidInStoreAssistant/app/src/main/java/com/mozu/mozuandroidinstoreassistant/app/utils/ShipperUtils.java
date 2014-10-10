package com.mozu.mozuandroidinstoreassistant.app.utils;

public class ShipperUtils {

    public static enum Shipper{
        UPS("http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums=%s"),
        DHL("http://track.dhl-usa.com/TrackByNbr.asp?ShipmentNumber=%s"),
        FEDEX("http://www.fedex.com/Tracking?action=track&tracknumbers=%s"),
        USPS("https://tools.usps.com/go/TrackConfirmAction_input?qtc_tLabels1=%s");

        private String mTrackingUrl;
        private Shipper(String trackingUrl){
            mTrackingUrl = trackingUrl;
        }

        public String getTrackingUrl(String trackingNummber){
            return String.format(mTrackingUrl,trackingNummber);
        }

    }
}
