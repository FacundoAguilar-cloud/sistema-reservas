package com.shops.microservices.msvc_shops.reponse;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ShopResponse {
private Long id;
private Long OwnerId;
private String name;
private String description;

private String adress;
private String city;
private String state;
private String country;

private String phone;
private String email;

//agenda y horarios
public enum WeekSchedule {
	MONDAY(LocalTime.of(8, 0), LocalTime.of(8, 0)),
    TUESDAY(LocalTime.of(8, 0), LocalTime.of(8, 0)),
    WEDNESDAY(LocalTime.of(8, 0), LocalTime.of(8, 0)),
    THURSDAY(LocalTime.of(8, 0), LocalTime.of(8, 0)),
    FRIDAY(LocalTime.of(8, 0), LocalTime.of(8, 0)),
    SATURDAY(LocalTime.of(8, 0), LocalTime.of(8, 0));



	private final LocalTime openingTime;
	private final LocalTime closingTime;

	WeekSchedule(LocalTime openingTime, LocalTime closingTime) {
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}

	public LocalTime getOpeningTime() {
		return openingTime;
	}

	public LocalTime getClosingTime() {
		return closingTime;
	}
}

//config negocio:
//se va a poder reservar con dos semanas de antelación
    @Column(name = "advance_booking_days", nullable = false)
    private Integer advanceBookingDays = 14;

    //se va a poder cancerlar sin una penalizacion 24 hs antes, de lo contrario pasa algo (VER DESPUES)
    @Column(name = "advance_cancellation_hours", nullable = false)
    private Integer advanceCancellationHours = 24;    

    //la puntuacion que van a poder darle a cada negocio, similiar a google reseñas
    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    private Integer totalReviews=0;

    @Enumerated(EnumType.STRING)    
    @Column(nullable = false)    
    private ShopType type;

    public enum ShopType{
        BARBERSHOP("Barbershop"),
        HAIR_SALON("Hair Salon"),
        BEAUTY_SALON("Beauty Salon"),
        SPA("Spa"),
        MIXED("Mixed");

        private final String displayName;

        ShopType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
       
    }


}
