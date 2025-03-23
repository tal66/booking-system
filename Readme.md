## Booking System


### Instructions

Found in 
[Instructions.md](Instructions.md)

### APIs: Assignment Requirements

- **Movie API**: Manages movies available on the platform.
- **Showtime API**: Manages movies showtime on the theaters.
- **Booking API**: Manages the movie tickets booking.

Assignment Requirements:

#### Movies  APIs

| API Description | Endpoint                         | Request Body                          | Response Status | Response Body |
|-----------------|----------------------------------|---------------------------------------|-----------------|---------------|
| Get all movies  | GET /movies/all                  | | 200 OK          | [ { "id": 12345, "title": "Sample Movie Title 1", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }, { "id": 67890, "title": "Sample Movie Title 2", "genre": "Comedy", "duration": 90, "rating": 7.5, "releaseYear": 2024 } ] |
| Add a movie     | POST /movies                     | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK          | { "id": 1, "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }|
| Update a movie  | POST /movies/update/{movieTitle} | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK          | |
| Delete          | DELETE /movies/{movieTitle}      | | 200 OK          | |

#### Showtimes APIs

| API Description            | Endpoint                           | Request Body                                                                                                                                      | Response Status | Response Body                                                                                                                                                                                                                                                                   |
|----------------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Get showtime by ID | GET /showtimes/{showtimeId} |                                                                                                                                                   | 200 OK | { "id": 1, "price":50.2, "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }                                                                                                                      | | Delete a restaurant        | DELETE /restaurants/{id}           |                                                                              | 204 No Content  |                                                                                                        |
| Add a showtime | POST /showtimes | { "movieId": 1, "price":20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK | { "id": 1, "price":50.2,"movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }                                                                                                                                    |
| Update a showtime | POST /showtimes/update/{showtimeId}| { "movieId": 1, "price":50.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK |                                                                                                                                                                                                                                                                                 |
| Delete a showtime | DELETE /showtimes/{showtimeId} |                                                                                                                                                   | 200 OK |                                                                                                                                                                                                                                                                                 |


#### bookings APIs

| API Description           | Endpoint       | Request Body                                     | Response Status | Response Body                                                                                                                                          |
|---------------------------|----------------|--------------------------------------------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Book a ticket | POST /bookings | { "showtimeId": 1, "seatNumber": 15 , userId:"84438967-f68f-4fa0-b620-0f08217e76af"} | 200 OK | { "bookingId":"d1a6423b-4469-4b00-8c5f-e3cfc42eacae" }                                                                                                 |



