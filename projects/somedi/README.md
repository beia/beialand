# BEIA APIs for SOMEDI project

This project requires authentication info that allows access to the following Google Cloud services:
* Cloud Translation API
* Cloud Natural Language

To generate these follow this [guide](https://cloud.google.com/docs/authentication/#getting_credentials_for_server-centric_flow).

After you have the JSON file containing the credentials, set it as a secret, named `google_cloud_auth` in your Docker stack.
