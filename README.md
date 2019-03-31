# Claim-It

## Description

Our app **Claim.it** aims at reducing the effort from bank's end wrt the 
vehicular insurance claim investigation and claiming procedure.

Currently banks hire investigators with the sole job of investigating 
the filed reports. The investigator goes works diligently to find out 
the middle ground for bank and the claimer. Many a times cases end up 
in court where both claimer and the bank incurs losses. Furthermore, 
the banks have to deal with multiple fraudery cases wherein the claimer 
tries to claim insurance for personal gains. Investigations in such 
cases lead to losses wrt both time and money. To counter this scenario 
we introduce **Claim.it**, an app that aims to simplify the process.

We utilise a few sensors such as accelerometer, gyroscope, GPS etc. 
to generate the required details pertaining to the accident. Furthermore, 
we add a 360 degree camera to capture 1 minute worth of buffer video 
which would be stored directly on a distributed storage solution 
network (to make sure that the data is not held by a single entity, 
banks being the entity in this case). To further keep things transparent, 
we generate the hash of the video and store it on the blockchain 
network. Thus, the video can be trusted to untouched post posting.

**Claim.it** is therefore a simplified approach to claiming vehicular 
insurance both for the claimers and the banks. It reduces the losses 
incurred by the bank by a significant margin and helping the authorized 
personal to make correct decisions based on the accident report generated 
by **Claim.it**!

## Building setup

### Pre Requisites

Claim.it makes use of a wide array of technologies. As a result we require 
you to install them prior to building and running our app.

```
For backend:
 * virtualenv (optional but recommended)
 * pip
 * flask, flask-restful, flask-marshmallow
 * solc, py-solc
 * web3
 * ganache-cli
 * truffle

For Android:
 * None really
```

### Build and Run

For Backend you will require to run centralized and decentralized sections 
separately.

### Centralized

Run:

```
$ cd Centralized
$ flask run --host=0.0.0.0 --port=80
```

If you have met the required pre-requisites, you should see a server hosted 
locally on port 80.

### Decentralized

We first require to run the `ganache-cli` prior to deploying the decentralized 
section. Run:

```
$ ganache-cli
```

Now, change directory to **Decentralized/flask** and run:

```
$ flask run --host=0.0.0.0 --port=1337
```

If you have met the required pre-requisites, you should see a server hosted 
locally on port 1337.

### Android

To simplify the user end, we have provided the link for the apk. Install the 
(APK)[https://drive.google.com/open?id=1he3rvAK6NW9y4zLg_o5k6_-q75IIRXCY] and play around.

## License

Claim.it is licensed under GNU GPL V3.0