 # Infinite Advancements Fix (IAF)

![License](https://img.shields.io/github/license/Sollace/IAF)
![](https://img.shields.io/badge/api-fabric-orange.svg)

This mod memely exists to fix a vanilla bug in 1.19.2 that prevents the game from loading with more than a certain
amount of advancements.

## The Problem

In 1.19.2 vanilla, Mojang coded advancements to update their visibility using a recursive method (PlayerAdvancementTracker#updateDisplay).
In this class, they iterate through every advancement that has changed/needs updating, and they call updateDisplay on it.
Update display then, after doing its work, recursively calls itself on the advancement's parent and its children,
to propagate changes across the tree as needed.

This method of updating the tree is perfectly reasonable for smaller datasets, like what Mojang has in vanilla, but as soon as you get
into larger modpacks that adds hundreds, maybe thousands, of advancements, as soon as players start unlocking those advancements,
they will eventually run into issues that will cause the game to crash with a stackoverflow exception any time they try to join that world.

## The Fix

The fix is pretty obvious (if you're me): Don't use recursion.

So that's what I did.

I've replaced the original recursive method calls with an iterative loop and a stack, and done it carefully to preserve the old behaviour
and (hopefully) retain compatibility with Architectury and Create (both of which have injection points into the start of this method).
