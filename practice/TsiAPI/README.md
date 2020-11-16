# TSI API TESTING

The TSI BlueSky™ Air Quality Monitor is a lightweight, laser-based particle instrument designed to simultaneously measure PM2.5 and PM10 mass concentrations, as well as temperature and relative humidity. In the following section we will test the TSI API and document the process. 

# Getting started
Download and install [Postman](https://www.postman.com/downloads/) on your pc.
Click on import, then provide the [JSON collection file](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/External%20API%20Demo%20(BEIA).postman_collection%20v11-3-20%20(1).json).
  
# Obtaining the auth token  
Fill in the client id and the client secret and send the request.</br></br>
  ![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/authToken.PNG?raw=true)<br/><br/>
Users will receive a bearer token that will be used for further requests using OAuth2.<br/></br>  
  ![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/authTokenResponse.PNG?raw=true)<br/><br/>
Be aware that the bearer token expires in time, in this case you must request another token. The token also expires if a new token is generated.<br/>
Example of bad token response:<br/><br/>
  ![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/unauthorized.PNG?raw=true)<br/><br/>

# Get owned devices
Before sending the request users must fill in the bearer token generated previously in the authorization section by selecting Bearer Token as authorization type.<br/><br/>
![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/devices.PNG?raw=true)<br/><br/>
The response is a list of owned devices.<br/><br/>
![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/deviceResponse.PNG?raw=true)<br/><br/>

# Telemetry
The devices measure air quality at a given frequency. The measured data saved on the servers can be accesed using the request below. Users can filter the retrieved data by setting the time interval, the response format, timezone, etc. More details about the data filtering can be seen found in the [TSI API documentation](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/TSI%20Link%20External%20API%20v11-3-20.pdf).<br/><br/>
![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/telemetry.PNG?raw=true)<br/><br/>
Users will receive a list of air quality data samples from different owned devices based on the given parameters.<br/><br/> 
![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/telemetryResponse.PNG?raw=true)<br/><br/>

# Node Red
We created a [Node Red flow](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/blueSkyFlow.json) for gathering the data from Beia's BlueSky sensor (using the TSI API) and displayed it on [Grafana](https://grafana.beia-telemetrie.ro/d/lQoTQWoMz/bluesky-tsi-device-data?orgId=1).<br/><br/>
![Alt text](https://github.com/beia/beialand/blob/CristianBalanean/practice/TsiAPI/ss/grafana.png?raw=true)<br/><br/>