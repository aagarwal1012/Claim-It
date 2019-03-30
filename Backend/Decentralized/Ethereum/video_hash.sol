pragma solidity ^0.5.6;

contract HashRecords {
    
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