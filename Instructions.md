
## Booking system

### Setup DB (Postgres)

#### Option 1: Docker (Recommended)

```bash
docker-compose up
```

Note: exposes port 5433 (because on my local 5432 is in use).

#### Option 2: Local Postgres
- run `CREATE DATABASE popcorn-palace;`
- change application.yaml datasource port to 5432


### Run

Run tests: 
`mvn test`

Run app: 
`mvn spring-boot:run`

Run example requests: 

(get one)
```bash
curl "http://localhost:8080/movies/thor"
```

(get all, paged)
```bash
curl "http://localhost:8080/movies/all_paged?page=1&size=3"
```

(book)
```bash
curl -X POST -H "Content-Type:application/json"  "http://localhost:8080/bookings" -d '{ "showtimeId": 1, "seatNumber": 15 , "userId":"79de72f4-3f10-49db-8564-5c39a366ce2e"}'
```

(add show)
```bash
curl -X POST -H "Content-Type:application/json"  "http://localhost:8080/showtimes" -d '{ "movieId": 1, "price":15.2, "theater": "Theater 5", "startTime": "2025-03-01T21:00:00.000Z", "endTime": "2025-03-01T23:00:00.000Z" }'
```

(search)
```bash
curl "http://localhost:8080/movies/search?title=avengers"
```