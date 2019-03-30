<!-- Copyright (C) 2019 Nikunj Gupta -->

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