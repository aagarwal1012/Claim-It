pragma solidity ^0.5.0;

contract VideoHash {
    
    string url;
    uint generated_hash;


    function setHash(string memory u, uint h) public {
        url = u;
        generated_hash = h;
    }

    function getHash() public view returns (string memory, uint) {
        return (
            url, generated_hash
        );
    }
}