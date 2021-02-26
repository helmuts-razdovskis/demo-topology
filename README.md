# Demo application
Sample network topology REST application.

## Windows usage:
### Build & Test
`./gradlew build`

### Test
`./gradlew test`

### Run
`./gradlew bootRun`

Sample API access page can be found at [http://localhost:8080/]()

## API
GET requests:
* `/devices` - find all devices in registry sorted by device type (Gateway > Switch > Access Point)
* `/devices/{mac}`- find device with specified MAC address
* `/topology` - retrieve full topology
* `/topology/{mac}`- retrieve topology starting from MAC address


POST requests:
* `/devices` - create a device (params as follows)
    * `deviceType` = `"Gateway" | "Switch" | "Access Point"` (required)
    * `address` - device address (required)
    * `uplink` - device uplink, if present