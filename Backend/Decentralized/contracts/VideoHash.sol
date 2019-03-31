pragma solidity ^0.5.0;

contract VideoHash {
    
    string url;
    string generated_hash;


    function setHash(string memory u, string memory h) public {
        url = u;
        generated_hash = h;
    }

    function getHash() public view returns (string memory, string memory) {
        return (
            url, generated_hash
        );
    }
}