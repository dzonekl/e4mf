# Welcome to the E4MF Project! 

What is it? 
-----------

E4MF is an implementation of the Extended Library example EMF Editor on Eclipse e4 Platform. 

The Extended library is a sample model, which demonstrates many different moddeling aspects. 
EMF Tooling, supports the code generation of an RCP Editor for editing an Extended Library Model. 
E4MF is a an Extended library RCP Editor using the Eclipse e4 Framework


Why is it?
----------

Someone at the recent Eclipse Conference asked, will an EMF Editor 
work in e4? The answer is yes, using the 3.x compatibility layer. 
I wanted to learn e4, and decided it would be fun to create e4mf 

Perhaps cool if some of this works ends-up in the official releases of 
EMF. Including the generation of a working RCP editor based on e4 for any arbitrary 
EMF model. 


How does it work?
--------------

Clone the repo, and import the projects in the *bundles* folder into your Eclipse (4.3) IDE Workspace. 


- Open the .product file

/org.eclipse.emf.examples.library.e4editor/org.eclipse.emf.examples.library.e4editor.product

- Check the product dependencies
. 
- Launch the product


This will open the E4mf editor for the Extended Library Model. . 


What's in the gitbox? 
---------------------

- An adaptation of the eclipse EMF plugins for e4.
- The Extended library editor in it's 3.x and 4.x version. 
- Additional e4 tooling for: 
       - An e4 Outline view (Can be used in any e4 App). 
       - An e4 Resources plugins with the original 3.x workbench image resources. 
       
 
What have you done????
----------------------

I have documented every single step taken to achieve this. 

[Read about the conversion process](http://modelmoo.blogspot.com/)


Gratitude! 

Various people on the Eclipse e4 forum. 
Ed Merks for mentoring the approach. 


Have fun! 
